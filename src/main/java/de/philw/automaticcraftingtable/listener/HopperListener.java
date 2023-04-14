package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.util.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HopperListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public HopperListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    public BukkitTask bukkitTask () {

        return bukkitTask();
    }

    /**
     * This is the
     * @param inventoryMoveItemEvent
     */
    @EventHandler
    public void onDropperSwitch(InventoryMoveItemEvent inventoryMoveItemEvent) {
        if (!(inventoryMoveItemEvent.getInitiator().getLocation().getWorld().getBlockAt(Objects.requireNonNull(inventoryMoveItemEvent.getDestination().getLocation())).getState() instanceof Hopper)) {
            return;
        }
        if (hopperIsFull((Hopper) inventoryMoveItemEvent.getInitiator().getLocation().getWorld().getBlockAt(inventoryMoveItemEvent.getDestination().getLocation()).getState(), inventoryMoveItemEvent.getItem())) {
            return;
        }
        if (inventoryMoveItemEvent.getInitiator().getType() != InventoryType.HOPPER) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(automaticCraftingTable, () -> {
            Hopper hopper =
                    (Hopper) inventoryMoveItemEvent.getInitiator().getLocation().getWorld().getBlockAt(inventoryMoveItemEvent.getDestination().getLocation()).getState();
            Block craftingTable = null;
            Direction direction = null;

            switch (hopper.getRawData()) {
                case 0, 8 -> { // Hopper is facing down
                    craftingTable = hopper.getWorld().getBlockAt(hopper.getLocation().subtract(0, 1, 0));
                    direction = Direction.DOWN;
                }
                case 2, 10 -> { // Hopper is facing north
                    craftingTable = hopper.getWorld().getBlockAt(hopper.getLocation().subtract(0, 0, 1));
                    direction = Direction.NORTH;
                }
                case 3, 11 -> { // Hopper is facing south
                    craftingTable = hopper.getWorld().getBlockAt(hopper.getLocation().add(0, 0, 1));
                    direction = Direction.SOUTH;
                }
                case 4, 12 -> { // Hopper is facing west
                    craftingTable = hopper.getWorld().getBlockAt(hopper.getLocation().subtract(1, 0, 0));
                    direction = Direction.WEST;
                }
                case 5, 13 -> { // Hopper is facing east
                    craftingTable = hopper.getWorld().getBlockAt(hopper.getLocation().add(1, 0, 0));
                    direction = Direction.EAST;
                }
            }

            if (craftingTable == null) {
                return;
            }
            if (craftingTable.getType() != Material.CRAFTING_TABLE) {
                return;
            }

            CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
            if (!craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation())) {
                craftingTableManager.addEmptyCraftingTable(craftingTable.getLocation());
                craftingTableManager.saveCraftingTables();
            }

            List<ItemStack> contents = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                contents.add(i, craftingTableManager.getItemFromIndex(craftingTable.getLocation(), i) == null ? null :
                        craftingTableManager.getItemFromIndex(craftingTable.getLocation(), i));
            }

            ItemStack wantItemStack = automaticCraftingTable.getRecipeUtil().getCraftResult(contents);

            if (wantItemStack == null) {
                return;
            }

            Hopper target = getNextTarget(direction, craftingTable, wantItemStack, hopper);
            if (target == null) {
                return;
            }

            boolean accepted = true;

            List<ItemStack> ingredientList =
                    automaticCraftingTable.getRecipeUtil().getIngredientList(craftingTable.getLocation());

            for (ItemStack itemStack : ingredientList) {
                if (!hopper.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    accepted = false;
                }
            }

            if (accepted) {
                wantItemStack.setAmount(1);
                target.getInventory().addItem(wantItemStack);
                for (ItemStack itemStack : ingredientList) {
                    hopper.getInventory().removeItem(itemStack);
                }
            }
        }, 4);

    }

    int i = 0;

    @EventHandler
    public void onHopperAddItem(InventoryInteractEvent inventoryClickEvent) {
        System.out.println("Fired" + i);
        i++;
    }

    /**
     * Checks if a hopper is Full
     *
     * @param hopper        The hopper you want to check
     * @param wantItemStack The item you want to transport (it is needed because it can be stacked)
     * @return If the hopper is full
     */

    private boolean hopperIsFull(Hopper hopper, ItemStack wantItemStack) {
        List<ItemStack> storageContents = new ArrayList<>();
        for (ItemStack isItemStack : hopper.getInventory().getStorageContents()) {
            if (isItemStack != null) {
                storageContents.add(isItemStack);
            }
        }
        if (storageContents.size() != 5) {
            return false;
        }
        for (ItemStack isItemStack : storageContents) {
            if (isItemStack.getMaxStackSize() == isItemStack.getAmount()) continue;
            wantItemStack.setAmount(isItemStack.getAmount());
            if (wantItemStack.isSimilar(isItemStack)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if another hopper is connected and if so returns it
     *
     * @param direction     The direction what the first hopper had
     * @param craftingTable The Crafting Table
     * @param itemStack     The item what should be transported
     * @param hopper        The hopper where the item is coming from
     * @return The hopper down (first choice) or to the hopper in the opposite direction (second choice) or to the
     * next connected hopper (third choice) or null
     */

    private Hopper getNextTarget(Direction direction, Block craftingTable, ItemStack itemStack, Hopper hopper) {
        Hopper target;

        if (craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(0, 1, 0)).getType() == Material.HOPPER) {
            target =
                    (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(0, 1, 0)).getState();
            if (!hopperIsFull(target, itemStack)) {
                return target;
            }
        }

        if (direction != Direction.DOWN) {
            if (direction == Direction.NORTH && craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(0, 0, 1)).getType() == Material.HOPPER) {
                target =
                        (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(0, 0, 1)).getState();
                if (!hopperIsFull(target, itemStack)) {
                    return target;
                }
            }
            if (direction == Direction.SOUTH && craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(0
                    , 0, 1)).getType() == Material.HOPPER) {
                target =
                        (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getState();
                if (!hopperIsFull(target, itemStack)) {
                    return target;
                }
            }
            if (direction == Direction.WEST && craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(1, 0, 0)).getType() == Material.HOPPER) {
                target =
                        (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(1, 0, 0)).getState();
                if (!hopperIsFull(target, itemStack)) {
                    return target;
                }
            }
            if (direction == Direction.EAST && craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(1,
                    0, 0)).getType() == Material.HOPPER) {
                target =
                        (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getState();
                if (!hopperIsFull(target, itemStack)) {
                    return target;
                }
            }
        }

        if (craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(0, 0, 1)).getType() == Material.HOPPER) {
            target =
                    (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(0, 0, 1)).getState();
            if (target.getLocation() != hopper.getLocation() && !hopperIsFull(target, itemStack)) {
                return target;
            }
        }
        if (craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getType() == Material.HOPPER) {
            target = (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getState();
            if (target.getLocation() != hopper.getLocation() && !hopperIsFull(target, itemStack)) {
                return target;
            }
        }
        if (craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(1, 0, 0)).getType() == Material.HOPPER) {
            target =
                    (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().subtract(1, 0, 0)).getState();
            if (target.getLocation() != hopper.getLocation() && !hopperIsFull(target, itemStack)) {
                return target;
            }
        }
        if (craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getType() == Material.HOPPER) {
            target = (Hopper) craftingTable.getWorld().getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getState();
            if (target.getLocation() != hopper.getLocation() && !hopperIsFull(target, itemStack)) {
                return target;
            }
        }

        return null;
    }

}
