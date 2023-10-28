package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class CraftingTableEditUIListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableEditUIListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method saves all the inventory changes a player made on a crafting table in craftingTables.yml.
     */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        if (!inventoryCloseEvent.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
                ConfigManager.getCraftingTableInventoryDisplay()))) {
            return;
        }
        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        Player player = (Player) inventoryCloseEvent.getPlayer();
        Location location = automaticCraftingTable.getCraftingTableManager().getLocationFromSavedString(
                Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(automaticCraftingTable, player.getUniqueId().toString()), PersistentDataType.STRING))
        );
        boolean empty = true;
        if (!craftingTableManager.isCraftingTableRegistered(location)) {
            craftingTableManager.addEmptyCraftingTable(location);
        }
        // Get the items from the inventory and store it in the craftingTable.yml
        for (int index = 0; index < 9; index++) {
            ItemStack itemStack = inventoryCloseEvent.getInventory().getItem(index);
            craftingTableManager.setItemToIndex(location,
                    index,
                    itemStack);
            if (itemStack != null) {
                empty = false;
            }
        }
        // If nothing is in a registered craftingTable why saving it? REMOVE! (only when separate crafting tables is turned off, because when on, we need to differ the normal ones from the acts and the acts have to be remembered in the file)
        if (empty && craftingTableManager.isCraftingTableRegistered(location) && !ConfigManager.isSeparateFromOtherCraftingTables()) {
            craftingTableManager.removeCraftingTable(location);
            craftingTableManager.saveCraftingTables();
            return;
        }
        if (empty && !ConfigManager.isSeparateFromOtherCraftingTables()) {
            return;
        }
        // Store the changes
        craftingTableManager.saveCraftingTables();
    }
}