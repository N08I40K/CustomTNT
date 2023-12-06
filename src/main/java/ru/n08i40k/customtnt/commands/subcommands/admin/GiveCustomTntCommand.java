package ru.n08i40k.customtnt.commands.subcommands.admin;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.customtnt.commands.SubCommand;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor;

import java.util.*;

public class GiveCustomTntCommand extends SubCommand {
    public GiveCustomTntCommand(@Nullable SubCommand parent) {
        super(parent);
    }

    @Override
    public @NotNull String getName() {
        return "giveCustomTnt";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Map<String, CustomTNTEntryAccessor.RegisteredTNT> registeredTnt = CustomTNTEntryAccessor.getRegisteredTntMap();

        String tntId = args.length == 0 ? null : args[0];

        if (tntId == null || !registeredTnt.containsKey(tntId)) {
            localeRequestBuilder.get("incorrect-tnt", String.join(", ", registeredTnt.keySet()))
                    .getSingle().sendMessage(sender);
            return false;
        }

        // Get player name

        String playerName = (args.length >= 2) ? args[1] : sender.getName();
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            localeRequestBuilder.get("incorrect-player-name").getSingle().sendMessage(sender);
            return false;
        }

        // Get amount

        int amount = 1;

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                localeRequestBuilder.get("incorrect-amount").getSingle().sendMessage(sender);
                return false;
            }
            if (amount < 0 || amount > 64) {
                localeRequestBuilder.get("incorrect-amount").getSingle().sendMessage(sender);
                return false;
            }
        }

        // Give signal

        CustomTNTEntryAccessor.RegisteredTNT tnt = registeredTnt.get(tntId);

        ItemStack customTnt = tnt.getNItemStack().getItemStack();
        customTnt.setAmount(amount);

        if (!player.getInventory().addItem(customTnt).isEmpty()) {
            localeRequestBuilder.get("full-inventory").getSingle().sendMessage(sender);
            return false;
        }

        String tntName = CustomTNTEntryAccessor.getGameName(tnt.getEntry()).get();

        localeRequestBuilder.get("successful", playerName, tntName, amount).getSingle().sendMessage(sender);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return getAutocompletion(args[0], CustomTNTEntryAccessor.getRegisteredTntMap().keySet());
        } else if (args.length == 2) {
            Set<String> players = new HashSet<>();

            plugin.getServer().getOnlinePlayers().forEach(
                    player -> players.add(player.getName())
            );
            return getAutocompletion(args[1], players);
        }

        return ImmutableList.of();
    }
}
