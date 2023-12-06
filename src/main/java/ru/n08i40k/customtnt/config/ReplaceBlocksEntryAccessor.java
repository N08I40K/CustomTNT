package ru.n08i40k.customtnt.config;

import meteordevelopment.orbit.EventHandler;
import org.bukkit.Material;
import ru.n08i40k.npluginlocale.event.LocaleReloadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplaceBlocksEntryAccessor {
    private static final Map<ReplaceBlocksEntry, List<Material>> fromMaterialCaches = new HashMap<>();

    public static List<Material> getFromMaterials(ReplaceBlocksEntry entry) {
        if (fromMaterialCaches.containsKey(entry))
            return fromMaterialCaches.get(entry);

        List<Material> materials = new ArrayList<>();

        for (String materialName : entry.getFrom())
            materials.add(Material.getMaterial(materialName));

        fromMaterialCaches.put(entry, materials);

        return materials;
    }

    public static void setFromMaterialNames(ReplaceBlocksEntry entry, List<Material> materials) {
        fromMaterialCaches.remove(entry); // remove old cache

        List<String> materialNames = new ArrayList<>();

        for (Material material : materials)
            materialNames.add(material.name());

        entry.setFrom(materialNames);

        getFromMaterials(entry); // update cache
    }


    public static Material getToMaterial(ReplaceBlocksEntry entry) {
        return Material.getMaterial(entry.getTo());
    }

    public static void setToMaterialName(ReplaceBlocksEntry entry, Material material) {
        entry.setTo(material.name());
    }

    @EventHandler
    public static void onLocaleReload(LocaleReloadEvent.Post event)
    {
        fromMaterialCaches.clear();
    }
}
