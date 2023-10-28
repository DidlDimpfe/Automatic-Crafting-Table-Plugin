package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Objects;

public class PlaceACTListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public PlaceACTListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @EventHandler
    public void onACTPlaced(BlockPlaceEvent e) {
        Block act = e.getBlockPlaced();

        if (act.getType() != Material.CRAFTING_TABLE) return;
        if (!Objects.requireNonNull(e.getItemInHand().getItemMeta()).getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', ConfigManager.getCraftingTableItemDisplay()))) return;

        // To here it is an act
        act.setMetadata(ACTBlockUTIL.metadataKey, ACTBlockUTIL.metadataValue);
        automaticCraftingTable.getCraftingTableManager().addEmptyCraftingTable(e.getBlock().getLocation());
        automaticCraftingTable.getCraftingTableManager().saveCraftingTables();
    }

}
