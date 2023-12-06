package ru.n08i40k.customtnt.napi;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import ru.n08i40k.customtnt.CustomTNTPlugin;
import ru.n08i40k.npluginapi.itemStack.NItemStack;

public class CustomTNTItemStack extends NItemStack {
    public CustomTNTItemStack(@NonNull ItemStack itemStack, @NonNull String id) {
        super(CustomTNTPlugin.getInstance().getNPlugin(), itemStack, id, true);
    }
}
