package de.philw.automaticcraftingtable.commands;

import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetACTCommand implements CommandExecutor {

    /**
     * This command gives the place the automatic crafting table item
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        player.getInventory().addItem(ACTBlockUTIL.getACT());

        return true;
    }
}
