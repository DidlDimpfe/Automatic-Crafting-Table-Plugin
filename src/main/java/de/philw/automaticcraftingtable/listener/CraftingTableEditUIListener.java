package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingTableEditUIListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableEditUIListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @EventHandler
    public void onCraftingTableEditUIListenerClicked(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory() == null) {
            return;
        }
        if (!inventoryClickEvent.getView().getTitle().endsWith("Automatic Workbench Recipe")) {
            return;
        }
        if (inventoryClickEvent.getClickedInventory() != inventoryClickEvent.getInventory()) {
            return;
        }
        if (inventoryClickEvent.getCurrentItem() != null) {
            ItemStack itemStack = inventoryClickEvent.getCurrentItem();


            if (itemStack.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                inventoryClickEvent.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        if (!inventoryCloseEvent.getView().getTitle().endsWith("Automatic Workbench Recipe")) {
            return;
        }
        for (int i : new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23}) {
            ItemStack itemStack = inventoryCloseEvent.getInventory().getItem(i);
            Location location =
                    automaticCraftingTable.getCraftingTableManager().getLocationFromSavedString(inventoryCloseEvent.getInventory().getItem(0).getItemMeta().getLocalizedName());
            automaticCraftingTable.getCraftingTableManager().addItemToIndex(location,
                    automaticCraftingTable.getCraftingTableManager().castFromBigInventoryToSmallInventory(i),
                    itemStack);
        }
        automaticCraftingTable.getCraftingTableManager().saveCraftingTables();
    }


}
