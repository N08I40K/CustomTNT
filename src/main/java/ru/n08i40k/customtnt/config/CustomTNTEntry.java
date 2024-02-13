package ru.n08i40k.customtnt.config;

import lombok.Getter;
import lombok.Setter;
import ru.n08i40k.npluginapi.custom.craft.NCraftRecipeEntry;

import java.util.List;

public class CustomTNTEntry {
    @Getter @Setter
    private String name;

    @Getter @Setter
    private boolean canExplodeInWater;
    @Getter @Setter
    private boolean canExplodeInLava;
    @Getter @Setter
    private boolean canExplodeInAir;

    @Getter @Setter
    private float damage;
    @Getter @Setter
    private float damageRadius;

    @Getter @Setter
    private float baseTntRadius;

    @Getter @Setter
    private List<ReplaceBlocksEntry> replaceBlockEntries;

    @Getter @Setter
    private NCraftRecipeEntry craftRecipe;


    public CustomTNTEntry() {
        name = "customtnt_example";

        damage = 20;
        baseTntRadius = 4;

        replaceBlockEntries = List.of(new ReplaceBlocksEntry());
        craftRecipe = new NCraftRecipeEntry();
    }
}
