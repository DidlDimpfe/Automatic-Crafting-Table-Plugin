package de.philw.automaticcraftingtable.commands;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GetACTCommand implements CommandExecutor {

    private final AutomaticCraftingTable automaticCraftingTable;

    public GetACTCommand (AutomaticCraftingTable automaticCraftingTable){
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This command gives the place the automatic crafting table item
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                if (canAddACT(player)) {
                    player.getInventory().addItem(ACTBlockUTIL.getACT());
                    player.sendMessage(ChatColor.GREEN + "Successfully given AutomaticCraftingTable to you");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "Your inventory is full");
                return false;
            } else if (args.length == 1) {
                Player target = null;
                for (Player possibleTarget: Bukkit.getOnlinePlayers()) {
                    if (possibleTarget.getName().equals(args[0])) {
                        target = possibleTarget;
                        break;
                    }
                }
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "This player is either not existing or not online");
                    return false;
                }
                if (canAddACT(target)) {
                    target.getInventory().addItem(ACTBlockUTIL.getACT());
                    player.sendMessage(ChatColor.GREEN + "Successfully given AutomaticCraftingTable to " + target.getName());
                    target.sendMessage(ChatColor.GREEN + player.getName() + " has given you an AutomaticCraftingTable");
                    return true;
                }
                player.sendMessage(target.getName() + (target.getName().endsWith("s") ? "'" : "'s" + " inventory is full"));
                return false;
            } else {
                player.sendMessage(ChatColor.RED + "Do /getACT (player)");
                return false;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (args.length != 1) {
                automaticCraftingTable.getLogger().info("Do: getACT <player>");
                return false;
            }
            Player target = null;
            for (Player possibleTarget: Bukkit.getOnlinePlayers()) {
                if (possibleTarget.getName().equals(args[0])) {
                    target = possibleTarget;
                    break;
                }
            }
            if (target == null) {
                automaticCraftingTable.getLogger().info(ChatColor.RED + "This player is either not existing or not online");
                return false;
            }
            if (canAddACT(target)) {
                target.getInventory().addItem(ACTBlockUTIL.getACT());
                automaticCraftingTable.getLogger().info("Successfully given AutomaticCraftingTable to " + target.getName());
                target.sendMessage(ChatColor.GREEN + "Console has given you an AutomaticCraftingTable");
                return true;
            }
            automaticCraftingTable.getLogger().info(target.getName() + (target.getName().endsWith("s") ? "'" : "'s" + " inventory is full"));
            return false;
        }
        return false;
    }

    private boolean canAddACT(Player player){
        Inventory inv = player.getInventory();
        for (ItemStack item: inv.getStorageContents()) {
            if(item == null) {
                return true;
            }
            ItemStack possibleACT = item.clone();
            possibleACT.setAmount(1);
            if (possibleACT.equals(ACTBlockUTIL.getACT()) && item.getAmount() < item.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

}