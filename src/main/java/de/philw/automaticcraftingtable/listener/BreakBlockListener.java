package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakBlockListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public BreakBlockListener (AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method checks if a registered crafting table block is broken.
     * If so, it removes all info from it of off the craftingTales.yml and gives the items in it back to the player.
     */

    @EventHandler
    public void onRegisteredCraftingTableBlockBreak(BlockBreakEvent blockBreakEvent) {
        Block craftingTable = blockBreakEvent.getBlock();
        if (craftingTable.getType() != Material.CRAFTING_TABLE) {
            return;
        }
        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        if (craftingTableManager.isCraftingTableNotRegistered(craftingTable.getLocation())) {
            return;
        }

        for (ItemStack itemStack: craftingTableManager.getItemsInCraftingTable(craftingTable.getLocation())) {
            craftingTable.getWorld().dropItemNaturally(craftingTable.getLocation(), itemStack);
        }

        craftingTableManager.removeCraftingTable(craftingTable.getLocation());
        craftingTableManager.saveCraftingTables();

    }

}