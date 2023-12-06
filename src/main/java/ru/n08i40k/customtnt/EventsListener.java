package ru.n08i40k.customtnt;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor;
import ru.n08i40k.customtnt.utils.PluginUse;
import ru.n08i40k.npluginapi.block.NBlock;
import ru.n08i40k.npluginapi.block.NBlockNBT;

public class EventsListener extends PluginUse implements Listener {
    public EventsListener() {}

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawn(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof TNTPrimed tntPrimed))
            return;

        Location blockLocation = entity.getLocation().toBlockLocation();
        Block block = blockLocation.getBlock();

        NBTCompound nbtCompound = NBlockNBT.getNPluginNBTCompound(NBlockNBT.getPersistentData(block));
        if (nbtCompound == null)
            return;

        NBlock nBlock = NBlockNBT.getNBlock(nbtCompound);
        if (nBlock.getNPlugin().getPlugin() != CustomTNTPlugin.getInstance())
            return;

        CustomTNTEntryAccessor.RegisteredTNT registeredTNT = CustomTNTEntryAccessor
                .getRegisteredTntMap().get(nBlock.getNResourceKey().getObjectId());

        registeredTNT.getNEntity().applyNBTCompound(entity);

        new NBTBlock(block).getData().clearNBT();
    }
}
