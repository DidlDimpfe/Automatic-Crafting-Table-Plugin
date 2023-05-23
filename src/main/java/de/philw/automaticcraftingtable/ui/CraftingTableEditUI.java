package de.philw.automaticcraftingtable.ui;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class CraftingTableEditUI {

    /**
     * The constructor opens the crafting table edit inventory
     *
     * @param player        The player who wants to open it
     * @param craftingTable The crafting table the player want to open.
     */

    public CraftingTableEditUI(Player player, AutomaticCraftingTable automaticCraftingTable, Block craftingTable,
                               boolean registered) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.DROPPER,
                ChatColor.translateAlternateColorCodes('&', ConfigManager.getCraftingTableDisplay()));


        if (registered) {
            for (int smallInventoryIndex = 0; smallInventoryIndex < 9; smallInventoryIndex++) {
                if (automaticCraftingTable.getCraftingTableManager().getItemFromIndex(craftingTable.getLocation(), smallInventoryIndex) != null) {
                    ItemStack itemStack = automaticCraftingTable.getCraftingTableManager().getItemFromIndex(craftingTable.getLocation(),
                            smallInventoryIndex);
                    inventory.setItem(smallInventoryIndex, itemStack);
                }
            }
        }
        NamespacedKey namespacedKey = new NamespacedKey(automaticCraftingTable, player.getUniqueId().toString());

        PersistentDataContainer playerPDC = player.getPersistentDataContainer();
        playerPDC.set(namespacedKey, PersistentDataType.STRING, automaticCraftingTable.getCraftingTableManager().getSavedLocation(craftingTable.getLocation()));

        player.openInventory(inventory);
    }

}
