package ru.n08i40k.customtnt.config;

import de.tr7zw.nbtapi.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import meteordevelopment.orbit.EventHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.customtnt.CustomTNTPlugin;
import ru.n08i40k.customtnt.napi.CustomTNTBlock;
import ru.n08i40k.customtnt.napi.CustomTNTEntity;
import ru.n08i40k.customtnt.napi.CustomTNTItemStack;
import ru.n08i40k.customtnt.utils.RussianNumbers;
import ru.n08i40k.npluginapi.NPluginApi;
import ru.n08i40k.npluginapi.craft.NCraftRecipe;
import ru.n08i40k.npluginapi.database.NBlockRegistry;
import ru.n08i40k.npluginapi.database.NCraftRecipeRegistry;
import ru.n08i40k.npluginapi.database.NEntityRegistry;
import ru.n08i40k.npluginapi.database.NItemStackRegistry;
import ru.n08i40k.npluginapi.plugin.NPlugin;
import ru.n08i40k.npluginconfig.Config;
import ru.n08i40k.npluginconfig.event.ConfigLoadEvent;
import ru.n08i40k.npluginlocale.*;
import ru.n08i40k.npluginlocale.Locale;
import ru.n08i40k.npluginlocale.event.LocaleReloadEvent;

import java.util.*;

public class CustomTNTEntryAccessor {
    @Getter
    @Setter
    public static class RegisteredTNT {
        private CustomTNTEntry entry;

        private CustomTNTItemStack nItemStack;
        private CustomTNTEntity nEntity;
        private CustomTNTBlock nBlock;
        private NCraftRecipe nCraftRecipe;
    }

    private static final LocaleRequestBuilder tntRequestBuilder =
            new LocaleRequestBuilder(null, "tnt");

    @Getter
    @NonNull
    private static final Map<String, RegisteredTNT> registeredTntMap = new HashMap<>();


    @EventHandler public static void onLocaleReloadPre(LocaleReloadEvent.Pre event) {
        reload(true);
    }
    @EventHandler public static void onLocaleReloadPost(LocaleReloadEvent.Post event) {
        reload(false);
    }
    @EventHandler public static void onConfigReloadPre(ConfigLoadEvent.Pre<MainConfig> event) {
        if (!event.getConfig().isLoaded())
            return;

        Config<MainConfig> config = event.getConfig();

        if (!config.isLoaded())
            return;

        if (!config.getData().isEnableCrafts())
            return;

        if (!Locale.isLocaleLoaded())
            return;

        reload(true);
    }
    @EventHandler public static void onConfigReloadPost(ConfigLoadEvent.Post<MainConfig> event) {
        if (!event.isSuccessful())
            return;

        if (!event.getConfig().getData().isEnableCrafts())
            return;

        if (!Locale.isLocaleLoaded())
            return;

        reload(false);
    }

    private static void reload(boolean isPre) {
        if (isPre)
            CustomTNTEntryAccessor.unloadAll();
        else
            CustomTNTEntryAccessor.loadAll();
    }

    public static LocaleRequestBuilder getLocaleBuilder(@NonNull CustomTNTEntry entry) {
        return tntRequestBuilder.extend(entry.getName());
    }

    public static SingleLocaleResult getGameName(@NonNull CustomTNTEntry entry) {
        return getLocaleBuilder(entry).get("name").getSingle();
    }

    public static MultipleLocaleResult getGameLore(@NonNull CustomTNTEntry entry) {
        LocaleResult localeResult = getLocaleBuilder(entry).get("lore");

        String damageRadius = RussianNumbers.getWord("blocks", entry.getDamageRadius());
        String baseTntRadius = RussianNumbers.getWord("blocks", entry.getBaseTntRadius());

        localeResult.format(Map.of(
                "damage", entry.getDamage(),
                "damage_radius", damageRadius,
                "base_tnt_radius", baseTntRadius));

        return localeResult.getMultiple();
    }

    public static ItemStack getItemStack(@NonNull CustomTNTEntry entry) {
        ItemStack itemStack = new ItemStack(Material.TNT);
        ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(getGameName(entry).getC());
        meta.lore(getGameLore(entry).getC());

        itemStack.setItemMeta(meta);

        NBT.modify(itemStack, nbt -> {
            nbt.setString("customTNT", entry.getName());
        });

        return itemStack;
    }

    // [UN]LOAD

    public static void unloadAll() {
        NPluginApi.getInstance().getNPluginManager().getNItemStackRegistry().removeAll(
                CustomTNTPlugin.getInstance().getNPlugin());
        NPluginApi.getInstance().getNPluginManager().getNCraftRecipeRegistry().removeAll(
                CustomTNTPlugin.getInstance().getNPlugin());

        registeredTntMap.clear();
    }

    public static void loadAll() {
        NPlugin nPlugin = CustomTNTPlugin.getInstance().getNPlugin();

        NItemStackRegistry      nItemStackRegistry      = NPluginApi.getInstance().getNPluginManager().getNItemStackRegistry();
        NBlockRegistry          nBlockRegistry          = NPluginApi.getInstance().getNPluginManager().getNBlockRegistry();
        NCraftRecipeRegistry    nCraftRecipeRegistry    = NPluginApi.getInstance().getNPluginManager().getNCraftRecipeRegistry();
        NEntityRegistry         nEntityRegistry         = NPluginApi.getInstance().getNPluginManager().getNEntityRegistry();

        CustomTNTPlugin.getInstance().getMainConfigData().getTntEntries().values()
                .forEach(entry -> {
                    RegisteredTNT tnt = new RegisteredTNT();

                    tnt.setEntry(entry);

                    registeredTntMap.put(entry.getName(), tnt);
                });

        registeredTntMap.forEach((key, tnt) -> {
            CustomTNTItemStack customTntItemStack = new CustomTNTItemStack(getItemStack(tnt.getEntry()), key);
            nItemStackRegistry.add(customTntItemStack);

            tnt.setNItemStack(customTntItemStack);
        });

        registeredTntMap.forEach((key, tnt) -> {
            CustomTNTBlock customTNTBlock = new CustomTNTBlock(key);
            nBlockRegistry.add(customTNTBlock);

            tnt.setNBlock(customTNTBlock);
        });

        registeredTntMap.forEach((key, tnt) -> {
            CustomTNTEntity customTntEntity = new CustomTNTEntity(key);
            nEntityRegistry.add(customTntEntity);

            tnt.setNEntity(customTntEntity);
        });

        registeredTntMap.forEach((key, tnt) -> {
            NCraftRecipe customTntCraftRecipe = new NCraftRecipe(nPlugin, key, tnt.getEntry().getCraftRecipe(), tnt.getNItemStack());
            nCraftRecipeRegistry.add(customTntCraftRecipe);

            tnt.setNCraftRecipe(customTntCraftRecipe);
        });
    }

    // GET ENTRY FROM OBJECT

    @Nullable
    public static String getNameFromItemStack(@NonNull ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        if (nbtItem.hasTag("customTNT"))
            return nbtItem.getString("customTNT");

        return null;
    }

    @Nullable
    public static String getNameFromBlock(@NonNull Block block) {
        NBTBlock nbtBlock = new NBTBlock(block);

        if (nbtBlock.getData().hasTag("customTNT"))
            return nbtBlock.getData().getString("customTNT");

        return null;
    }

    @Nullable
    public static String getNameFromEntity(@NonNull Entity entity) {
        NBTCompound nbtCompound = new NBTEntity(entity).getPersistentDataContainer();

        if (nbtCompound.hasTag("customTNT"))
            return nbtCompound.getString("customTNT");

        return null;
    }

    @Nullable
    public static CustomTNTEntry getEntryFromName(@Nullable String name) {
        if (name == null)
            return null;

        CustomTNTEntriesMap entriesMap = CustomTNTPlugin.getInstance().getMainConfigData().getTntEntries();

        if (entriesMap.containsKey(name))
            return entriesMap.get(name);

        return null;
    }

    @Nullable
    public static CustomTNTEntry getEntryFromItemStack(@NonNull ItemStack itemStack) {
        return getEntryFromName(getNameFromItemStack(itemStack));
    }

    @Nullable
    public static CustomTNTEntry getEntryFromBlock(@NonNull Block block) {
        return getEntryFromName(getNameFromBlock(block));
    }

    @Nullable
    public static CustomTNTEntry getEntryFromEntity(@NonNull Entity entity) {
        return getEntryFromName(getNameFromEntity(entity));
    }
}
