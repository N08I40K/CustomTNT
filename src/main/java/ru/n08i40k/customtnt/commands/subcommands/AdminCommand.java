package ru.n08i40k.customtnt.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.customtnt.commands.SubCommand;
import ru.n08i40k.customtnt.commands.subcommands.admin.ConfigReloadCommand;
import ru.n08i40k.customtnt.commands.subcommands.admin.GetBlockNbtCommand;
import ru.n08i40k.customtnt.commands.subcommands.admin.GiveCustomTntCommand;

public class AdminCommand extends SubCommand {
    public AdminCommand(@Nullable SubCommand parent) {
        super(parent);

        subcommands.put(new ConfigReloadCommand(this));
        subcommands.put(new GiveCustomTntCommand(this));
        subcommands.put(new GetBlockNbtCommand(this));
    }

    @Override
    public @NotNull String getName() {
        return "admin";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        localeRequestBuilder.get("select-action", String.join(", ", subcommands.keySet())).getSingle().sendMessage(sender);
        return false;
    }
}
