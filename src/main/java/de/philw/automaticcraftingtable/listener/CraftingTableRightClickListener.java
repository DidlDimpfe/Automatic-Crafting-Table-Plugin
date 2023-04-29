package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.ui.CraftingTableEditUI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CraftingTableRightClickListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableRightClickListener(AutomaticCraftingTable automaticCraftingTable) {
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

        // To here it is requested

        if (ConfigManager.getUINeedPermission()) {

            if (!player.hasPermission("crafting-table-ui")) {
                return;
            }
        }

        playerInteractEvent.setCancelled(true);

        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();

        new CraftingTableEditUI(player, craftingTableManager, craftingTable,
                craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation()));
    }
}