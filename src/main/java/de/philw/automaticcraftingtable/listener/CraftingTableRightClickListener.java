package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.ui.CraftingTableEditUI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CraftingTableRightClickListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableRightClickListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @EventHandler
    public void onCraftingTableConfigRequested(PlayerInteractEvent playerInteractEvent) {

        if (playerInteractEvent.getHand() != (EquipmentSlot.HAND)) {
            return;
        }

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

        playerInteractEvent.setCancelled(true);

        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        if (!craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation())) {
            craftingTableManager.addEmptyCraftingTable(craftingTable.getLocation());
            craftingTableManager.saveCraftingTables();
        }

        new CraftingTableEditUI(player, craftingTableManager, craftingTable);

    }

}