package ru.n08i40k.customtnt.config;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ReplaceBlocksEntry {
    private List<String> from;
    private String to;

    private float radius;

    ReplaceBlocksEntry() {
        from = List.of(Material.STONE.name());
        to = Material.WATER.name();

        radius = 4;
    }

    public List<String> getFrom() {
        return from;
    }

    public void setFrom(List<String> from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
