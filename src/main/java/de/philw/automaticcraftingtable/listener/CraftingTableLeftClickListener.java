package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.ui.CraftingTableEditUI;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CraftingTableLeftClickListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableLeftClickListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method opens the crafting table edit ui, when a player sneaks and left clicks to a crafting table.
     */

    @EventHandler
    public void onCraftingTableConfigRequested(PlayerInteractEvent playerInteractEvent) {

        Player player = playerInteractEvent.getPlayer();

        if (!player.isSneaking()) {
            return;
        }

        Block craftingTable = playerInteractEvent.getClickedBlock();

        if (craftingTable == null) {
            return;
        }
        if (!craftingTable.getType().equals(Material.CRAFTING_TABLE)) {
            return;
        }

        if (!playerInteractEvent.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (ConfigManager.isSeparateFromOtherCraftingTables() && !craftingTable.hasMetadata(ACTBlockUTIL.metadataKey)) {
            return;
        }

        // To here it is requested

        playerInteractEvent.setCancelled(true);

        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();

        new CraftingTableEditUI(player, automaticCraftingTable, craftingTable,
                craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation()));
    }
}