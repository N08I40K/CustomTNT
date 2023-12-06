package ru.n08i40k.customtnt.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PluginPermissionBuilder {
    @Nullable
    private final PluginPermissionBuilder parent;

    @Nullable
    private String currentPermission;

    public PluginPermissionBuilder(@Nullable PluginPermissionBuilder parent, @Nullable String currentPermission) {
        this.currentPermission = currentPermission;
        this.parent = parent;
    }

    public PluginPermissionBuilder(@Nullable PluginPermissionBuilder parent) {
        this(parent, null);
    }

    protected String get() {
        return parent == null ? currentPermission : parent.get() + "." + currentPermission;
    }

    public boolean check(CommandSender sender) {
        return check(sender, null);
    }

    public PluginPermissionBuilder extend(String appendPermission) {
        return new PluginPermissionBuilder(this, appendPermission);
    }

    public boolean check(CommandSender sender, @Nullable String append) {
        if (!(sender instanceof Player))
            return true;
        if (sender.isOp())
            return true;

        return sender.hasPermission(get() + (append == null ? "" : "." + append));
    }
}
