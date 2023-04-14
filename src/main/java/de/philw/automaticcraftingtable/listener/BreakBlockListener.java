package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakBlockListener implements Listener {

    private AutomaticCraftingTable automaticCraftingTable;

    public BreakBlockListener (AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Block craftingTable = blockBreakEvent.getBlock();
        if (craftingTable.getType() != Material.CRAFTING_TABLE) {
            return;
        }
        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        if (!craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation())) {
            return;
        }

        for (ItemStack itemStack: craftingTableManager.getItemsInCraftingTable(craftingTable.getLocation())) {
            craftingTable.getWorld().dropItemNaturally(craftingTable.getLocation(), itemStack);
        }

        craftingTableManager.removeWorkbench(craftingTable.getLocation());
        craftingTableManager.saveCraftingTables();

    }

}