package ru.n08i40k.customtnt.commands.subcommands.admin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.customtnt.commands.SubCommand;

public class ConfigReloadCommand extends SubCommand {
    public ConfigReloadCommand(@Nullable SubCommand parent) {
        super(parent);
    }

    @Override
    public @NotNull String getName() {
        return "reload";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        plugin.getMainConfig().load();

        localeRequestBuilder.get("has-been-reloaded").getSingle().sendMessage(sender);
        return true;
    }
}
