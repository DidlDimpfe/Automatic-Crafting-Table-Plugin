package de.philw.automaticcraftingtable.ui;

import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftingTableEditUI {

    public CraftingTableEditUI(Player player, CraftingTableManager craftingTableManager, Block craftingTable) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Automatic Workbench Recipe");

        for (int i: new int[]{0 ,1, 2, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 24, 25, 26}) {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLocalizedName(craftingTableManager.getSavedLocation(craftingTable.getLocation()));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
        }

        for (int i = 0; i < 9; i++) {
            if (craftingTableManager.getItemFromIndex(craftingTable.getLocation(), i) != null) {
                ItemStack itemStack = craftingTableManager.getItemFromIndex(craftingTable.getLocation(), i);
                inventory.setItem(craftingTableManager.castFromSmallInventoryToBigInventory(i), itemStack);
            }
        }
        player.openInventory(inventory);
    }

}
