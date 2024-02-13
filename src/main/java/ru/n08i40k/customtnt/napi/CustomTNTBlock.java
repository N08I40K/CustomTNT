package ru.n08i40k.customtnt.napi;

import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.NBTCompound;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.n08i40k.customtnt.CustomTNTPlugin;
import ru.n08i40k.customtnt.config.CustomTNTEntry;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor;
import ru.n08i40k.npluginapi.custom.block.NBlock;
import ru.n08i40k.npluginapi.custom.block.NBlockNBT;
import ru.n08i40k.npluginapi.event.block.*;
import ru.n08i40k.npluginapi.resource.NResourceGroup;
import ru.n08i40k.npluginapi.resource.NResourceKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomTNTBlock extends NBlock {
    private static final Map<Block, Hologram> BLOCK_HOLOGRAM_MAP = new HashMap<>();

    public CustomTNTBlock(@NonNull String id) {
        super(CustomTNTPlugin.getInstance().getNPlugin(), id, Material.TNT,
                new NResourceKey(CustomTNTPlugin.getInstance().getNPlugin(), NResourceGroup.ITEM, id).toString());
    }

    private void createHologram(Block block) {
        NBTCompound nbtCompound = NBlockNBT.getNPluginNBTCompound(block);
        assert nbtCompound != null;

        if (!nbtCompound.hasTag("hologram-uuid"))
            nbtCompound.setUUID("hologram-uuid", UUID.randomUUID());
        UUID hologramUuid = nbtCompound.getUUID("hologram-uuid");

        Hologram hologram = BLOCK_HOLOGRAM_MAP.get(block);

        if (hologram != null) {
            hologram.destroy();
            hologram.unregister();
        }

        CustomTNTEntry entry = CustomTNTEntryAccessor.getEntryFromName(this.getNResourceKey().getObjectId());
        Preconditions.checkNotNull(entry, "Can't find CustomTNTEntry with id %s",
                this.getNResourceKey().getObjectId());

        assert hologramUuid != null;
        hologram = DHAPI.createHologram(hologramUuid.toString(),
                block.getLocation().toCenterLocation().add(0, 1.5f, 0),
                List.of(CustomTNTEntryAccessor.getGameName(entry).get()));

        BLOCK_HOLOGRAM_MAP.put(block, hologram);

        hologram.enable();
        hologram.showAll();
    }

    private void removeHologram(Block block) {
        Hologram hologram = BLOCK_HOLOGRAM_MAP.get(block);

        if (hologram == null)
            return;

        hologram.destroy();
        hologram.unregister();

        BLOCK_HOLOGRAM_MAP.remove(block);
    }

    @Override
    public void onPlace(NBlockPlaceEvent event) {
        super.onPlace(event);

        createHologram(event.getBlock());
    }

    @Override
    public void onChunkLoad(NBlockChunkLoadEvent event) {
        super.onChunkLoad(event);

        createHologram(event.getBlock());
    }

    @Override
    public void onBreak(NBlockBreakEvent event) {
        super.onBreak(event);

        removeHologram(event.getBlock());
    }

    @Override
    public void onChunkUnload(NBlockChunkUnloadEvent event) {
        super.onChunkUnload(event);

        removeHologram(event.getBlock());
    }

    @Override
    public void onTNTPrime(NBlockTNTPrimeEvent event) {
        super.onTNTPrime(event);

        removeHologram(event.getBlock());
    }
}
