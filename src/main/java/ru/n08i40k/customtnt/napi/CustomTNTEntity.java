package ru.n08i40k.customtnt.napi;

import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import ru.n08i40k.customtnt.CustomTNTPlugin;
import ru.n08i40k.customtnt.config.CustomTNTEntry;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor.RegisteredTNT;
import ru.n08i40k.customtnt.config.ReplaceBlocksEntry;
import ru.n08i40k.customtnt.config.ReplaceBlocksEntryAccessor;
import ru.n08i40k.npluginapi.custom.entity.NEntity;
import ru.n08i40k.npluginapi.custom.entity.NEntityNBT;
import ru.n08i40k.npluginapi.event.entity.NEntityAddToWorldEvent;
import ru.n08i40k.npluginapi.event.entity.NEntityExplodeEvent;
import ru.n08i40k.npluginapi.event.entity.NEntityTickEvent;
import ru.n08i40k.npluginlocale.Locale;

import java.util.*;


public class CustomTNTEntity extends NEntity<TNTPrimed> {

    public CustomTNTEntity(@NonNull String id) {
        super(CustomTNTPlugin.getInstance().getNPlugin(), id, TNTPrimed.class);
    }

    @Override
    public void onExplode(NEntityExplodeEvent nEvent) {
        TNTPrimed tntPrimed = (TNTPrimed) nEvent.getEntity();

        Hologram hologram = ENTITY_HOLOGRAM_MAP.get(tntPrimed);

        hologram.destroy();
        hologram.unregister();


        EntityExplodeEvent event = nEvent.getBukkitEvent();
        if (event.isCancelled())
            return;

        Location    location    = tntPrimed.getLocation();
        Block       block       = location.getBlock();
        Material    material    = block.getType();

        RegisteredTNT registeredTNT = CustomTNTEntryAccessor.getRegisteredTntMap()
                .get(getNResourceKey().getObjectId());

        CustomTNTEntry tntEntry = registeredTNT.getEntry();

        float damageRadius = tntEntry.getDamageRadius();
        if (damageRadius > 0.f && tntEntry.getDamage() > 0.f) {
            location.getWorld().getLivingEntities().forEach(worldEntity -> {
                double distance = worldEntity.getLocation().distance(location);

                if (distance > damageRadius)
                    return;

                worldEntity.damage(tntEntry.getDamage() * (1.f - (distance / damageRadius)));
            });
        }

        if (!tntEntry.isCanExplodeInAir() && material == Material.AIR)
            return;

        if (!tntEntry.isCanExplodeInWater() && material == Material.WATER)
            return;

        if (!tntEntry.isCanExplodeInLava() && material == Material.LAVA)
            return;

        for (ReplaceBlocksEntry entry : tntEntry.getReplaceBlockEntries()) {
            List<Material> fromMaterials = ReplaceBlocksEntryAccessor.getFromMaterials(entry);
            Material toMaterial = ReplaceBlocksEntryAccessor.getToMaterial(entry);

            List<Location> sphere = generateSphere(location, (int) entry.getRadius(), false);

            for (Location sphereLocation : sphere) {
                Block sphereBlock = sphereLocation.getBlock();
                Material sphereBlockMaterial = sphereBlock.getType();

                for (Material fromMaterial : fromMaterials) {
                    if (sphereBlockMaterial == fromMaterial) {
                        new NBTBlock(sphereBlock).getData().clearNBT();
                        sphereBlock.setType(toMaterial);
                    }
                }
            }
        }

        float baseTntRadius = tntEntry.getBaseTntRadius();
        if (baseTntRadius > 0.f)
            location.createExplosion(baseTntRadius);

        event.setCancelled(true);


    }

    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
        if (centerBlock == null) {
            return new ArrayList<>();
        }

        List<Location> circleBlocks = new ArrayList<>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x)) + ((bz-z) * (bz-z)) + ((by-y) * (by-y));

                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

                        Location l = new Location(centerBlock.getWorld(), x, y, z);

                        circleBlocks.add(l);

                    }

                }
            }
        }

        return circleBlocks;
    }

    private static final Map<Entity, Hologram> ENTITY_HOLOGRAM_MAP = new HashMap<>();

    private void renameHologram(TNTPrimed tntPrimed, Hologram hologram) {
        DHAPI.setHologramLine(hologram, 1,
                Locale.getInstance().get("hologram.timer", Math.round((float) tntPrimed.getFuseTicks() / 2.f) / 10).getSingle().get());
    }

    @Override
    public void onAddToWorld(NEntityAddToWorldEvent event) {
        super.onAddToWorld(event);

        // Set hologram uuid to entity
        TNTPrimed tntPrimed = (TNTPrimed) event.getEntity();
        NBTCompound nbtCompound = NEntityNBT.getNBTCompound(tntPrimed);

        assert nbtCompound != null;
        UUID hologramUuid = UUID.randomUUID();
        nbtCompound.setUUID("hologram-uuid", hologramUuid);


        // Get CustomTNTEntry for game name
        CustomTNTEntry entry = CustomTNTEntryAccessor.getEntryFromName(this.getNResourceKey().getObjectId());
        Preconditions.checkNotNull(entry, "Can't find CustomTNTEntry with id %s",
                this.getNResourceKey().getObjectId());


        // Creating hologram
        Hologram hologram = DHAPI.createHologram(hologramUuid.toString(),
                tntPrimed.getLocation().toCenterLocation().add(0, 2.f, 0),
                List.of(CustomTNTEntryAccessor.getGameName(entry).get(), ""));
        renameHologram(tntPrimed, hologram); // update last line

        ENTITY_HOLOGRAM_MAP.put(tntPrimed, hologram);

        hologram.enable();
        hologram.showAll();
    }

    @Override
    public void onTick(NEntityTickEvent event) {
        super.onTick(event);

        TNTPrimed tntPrimed = (TNTPrimed) event.getEntity();
        Hologram hologram = ENTITY_HOLOGRAM_MAP.get(tntPrimed);

        renameHologram((TNTPrimed) event.getEntity(), hologram);


        Location tntLocation = tntPrimed.getLocation();

        DHAPI.moveHologram(hologram, tntLocation
                .add(tntPrimed.getVelocity())
                .add(0, 2, 0));
    }
}
