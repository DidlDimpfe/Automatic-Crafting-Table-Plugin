package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CraftingTableEditUIListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableEditUIListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method will cancel the inventory click event, if the player clicks on a space block.
     */

    @EventHandler
    public void onCraftingTableEditUIGlassPaneClicked(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory() == null) {
            return;
        }
        if (!inventoryClickEvent.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
                ConfigManager.getCraftingTableDisplay()))) {
            return;
        }
        if (inventoryClickEvent.getClickedInventory() != inventoryClickEvent.getInventory()) {
            return;
        }
        if (inventoryClickEvent.getCurrentItem() != null) {
            ItemStack itemStack = inventoryClickEvent.getCurrentItem();
            if (itemStack.getType() == Material.BLACK_STAINED_GLASS_PANE &&
                    Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName().
                            equals(ChatColor.translateAlternateColorCodes('&', ConfigManager.getSpaceDisplay()))) {
                inventoryClickEvent.setCancelled(true);
            }
        }
    }

    /**
     * This method saves all the inventory changes a player made on a crafting table in craftingTables.yml.
     */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        if (!inventoryCloseEvent.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
                ConfigManager.getCraftingTableDisplay()))) {
            return;
        }
        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        Location location = craftingTableManager.getLocationFromSavedString(
                Objects.requireNonNull(Objects.requireNonNull(inventoryCloseEvent.getInventory().getItem(0)).
                        getItemMeta()).getLocalizedName());
        boolean empty = true;
        // Get the items from the inventory and store it in the craftingTable.yml
        for (int bigInventoryIndex : new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23}) {
            ItemStack itemStack = inventoryCloseEvent.getInventory().getItem(bigInventoryIndex);
            craftingTableManager.setItemToIndex(location,
                    craftingTableManager.castFromBigInventoryToSmallInventory(bigInventoryIndex),
                    itemStack);
            if (itemStack != null) {
                empty = false;
            }
        }
        // If nothing is in a registered craftingTable why saving it? REMOVE!
        if (empty && craftingTableManager.isCraftingTableRegistered(location)) {
            craftingTableManager.removeCraftingTable(location);
            craftingTableManager.saveCraftingTables();
            return;
        }
        if (empty) {
            return;
        }
        // Store the changes
        if (!craftingTableManager.isCraftingTableRegistered(location)) {
            craftingTableManager.addEmptyCraftingTable(location);
            craftingTableManager.saveCraftingTables();
        }
        craftingTableManager.saveCraftingTables();
    }
}