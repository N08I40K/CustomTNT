package ru.n08i40k.customtnt.commands.subcommands.admin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.customtnt.commands.SubCommand;
import ru.n08i40k.customtnt.config.CustomTNTEntry;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor;

public class GetBlockNbtCommand extends SubCommand {
    public GetBlockNbtCommand(@Nullable SubCommand parent) {
        super(parent);
    }

    @Override
    public @NotNull String getName() {
        return "getBlockNbt";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            locale.get("only-in-game").getSingle().sendMessage(sender);
            return false;
        }

        Player player = (Player) sender;

        RayTraceResult result = player.rayTraceBlocks(10.f);

        if (result == null)
            return false;

        Block block = result.getHitBlock();

        if (block == null)
            return false;

        if (block.getType() != Material.TNT)
            return false;

        CustomTNTEntry tntEntry = CustomTNTEntryAccessor.getEntryFromBlock(block);

        if (tntEntry == null)
        {
            localeRequestBuilder.get("no-nbt-found").getSingle().sendMessage(sender);
            return false;
        }


        localeRequestBuilder.get("nbt-found",
                CustomTNTEntryAccessor.getGameName(tntEntry).get(), tntEntry.getName()).getSingle().sendMessage(sender);

        return true;
    }
}
