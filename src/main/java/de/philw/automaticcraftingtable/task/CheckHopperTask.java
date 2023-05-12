package de.philw.automaticcraftingtable.task;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.util.Direction;
import de.philw.automaticcraftingtable.util.StackItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckHopperTask implements Runnable {

    private final AutomaticCraftingTable automaticCraftingTable;
    private final CraftingTableManager craftingTableManager;

    public CheckHopperTask(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
        this.craftingTableManager = automaticCraftingTable.getCraftingTableManager();
    }

    /**
     * The run method is the "main" method of the plugin. It checks every registered crafting table and when
     * there is are hoppers connected, who have enough items for the wanted recipe in it the crafting table gives the
     * wanted item to the next connected hopper and deletes the ingredients form the other hoppers.
     */

    @Override
    public void run() {
        for (String savedLocations : automaticCraftingTable.getCraftingTableManager().getLocations()) {
            Location location = craftingTableManager.getLocationFromSavedString(savedLocations);
            if (location == null) {
                continue;
            }
            Block craftingTable = Objects.requireNonNull(location.getWorld()).getBlockAt(location);
            if (craftingTable.getType() != Material.CRAFTING_TABLE) {
                continue;
            }
            ArrayList<Hopper> fromHoppers = getHoppersWhereItemComesFrom(craftingTable);
            ArrayList<ItemStack> itemsInFromHoppers = new ArrayList<>();

            for (Hopper fromHopper: fromHoppers) {
                for (ItemStack itemStack: fromHopper.getInventory().getContents()) {
                    if (itemStack != null) itemsInFromHoppers.add(itemStack.clone()); // Without the .clone() are many errors!
                }
            }

            itemsInFromHoppers = StackItems.combine(itemsInFromHoppers);

            CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();

            List<ItemStack> craftingTableContents = new ArrayList<>();

            for (int index = 0; index < 9; index++) {
                craftingTableContents.add(index, craftingTableManager.getItemFromIndex(craftingTable.getLocation(), index) == null ? null :
                        craftingTableManager.getItemFromIndex(craftingTable.getLocation(), index));
            }

            ItemStack wantItemStack = automaticCraftingTable.getRecipeUtil().getCraftResult(craftingTableContents);

            if (wantItemStack == null) {
                continue;
            }

            Hopper toHopper = getNextTarget(craftingTable, wantItemStack);

            if (toHopper == null) {
                continue;
            }

            boolean accepted = true;

            List<ItemStack> ingredientList =
                    automaticCraftingTable.getRecipeUtil().getIngredientList(craftingTable.getLocation());

            for (ItemStack itemStack : ingredientList) {
                if (!itemStackListContainsAtLeast(itemsInFromHoppers, itemStack)) {
                        accepted = false;
                }
            }

            if (accepted) {
                toHopper.getInventory().addItem(wantItemStack);
                for (ItemStack itemStack : ingredientList) {
                    for (int i = 1; i<=itemStack.getAmount(); i++) {
                        for (Hopper fromHopper: fromHoppers) {
                            ItemStack testItemStack = itemStack.clone();
                            testItemStack.setAmount(1);
                            if (fromHopper.getInventory().containsAtLeast(testItemStack, 1)) {
                                fromHopper.getInventory().removeItem(testItemStack);
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * Checks if a hopper is Full
     *
     * @param hopper        The hopper you want to check
     * @param wantItemStack The item you want to transport (it is needed because it can be stacked)
     * @return If the hopper is full
     */

    private boolean hopperIsNotFull(Hopper hopper, ItemStack wantItemStack) {
        List<ItemStack> storageContents = new ArrayList<>();
        for (ItemStack isItemStack : hopper.getInventory().getStorageContents()) {
            if (isItemStack != null) {
                storageContents.add(isItemStack);
            }
        }
        if (storageContents.size() != 5) {
            return true;
        }
        for (ItemStack isItemStack : storageContents) {
            if (isItemStack.getMaxStackSize() == isItemStack.getAmount()) continue;
            wantItemStack.setAmount(isItemStack.getAmount());
            if (wantItemStack.isSimilar(isItemStack)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if another hopper is connected and if so returns it
     *
     * @param craftingTable The Crafting Table
     * @param wantItemStack The item what should be transported
     * @return The hopper down (first choice) or to the next connected hopper (second choice) or null
     */

    private Hopper getNextTarget(Block craftingTable, ItemStack wantItemStack) {
        Hopper target;
        World world = craftingTable.getWorld();

        if (world.getBlockAt(craftingTable.getLocation().subtract(0, 1, 0)).getType() == Material.HOPPER) {
            target =
                    (Hopper) world.getBlockAt(craftingTable.getLocation().subtract(0, 1, 0)).getState();
            if (hopperIsNotFull(target, wantItemStack)) {
                return target;
            }
        }

        if (world.getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getType() == Material.HOPPER) {
            Hopper hopper =
                    (Hopper) world.getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getState();
            if (!hopperIsFacing(hopper, Direction.WEST) && hopperIsNotFull(hopper, wantItemStack)) {
                return hopper;
            }
        }

        if (world.getBlockAt(craftingTable.getLocation().subtract(1, 0, 0)).getType() == Material.HOPPER) {
            Hopper hopper = (Hopper) world.getBlockAt(craftingTable.getLocation().subtract(1, 0,
                    0)).getState();
            if (!hopperIsFacing(hopper, Direction.EAST) && hopperIsNotFull(hopper, wantItemStack)) {
                return hopper;
            }
        }
        if (world.getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getType() == Material.HOPPER) {
            Hopper hopper =
                    (Hopper) world.getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getState();
            if (!hopperIsFacing(hopper, Direction.NORTH) && hopperIsNotFull(hopper, wantItemStack)) {
                return hopper;
            }
        }
        if (world.getBlockAt(craftingTable.getLocation().subtract(0, 0, 1)).getType() == Material.HOPPER) {
            Hopper hopper = (Hopper) world.getBlockAt(craftingTable.getLocation().subtract(0, 0,
                    1)).getState();
            if (!hopperIsFacing(hopper, Direction.SOUTH) && hopperIsNotFull(hopper, wantItemStack)) {
                return hopper;
            }
        }
        return null;
    }

    /**
     * @return all the hoppers that go into a crafting table.
     */

    private ArrayList<Hopper> getHoppersWhereItemComesFrom(Block craftingTable) {
        ArrayList<Hopper> hoppersWhereItemsComesFrom = new ArrayList<>();
        World world = craftingTable.getWorld();
        if (world.getBlockAt(craftingTable.getLocation().add(0, 1, 0)).getType() == Material.HOPPER) {
            Hopper hopper =
                    (Hopper) world.getBlockAt(craftingTable.getLocation().add(0, 1, 0)).getState();
            if (hopperIsFacing(hopper, Direction.DOWN)) {
                hoppersWhereItemsComesFrom.add(hopper);
            }
        }
        if (world.getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getType() == Material.HOPPER) {
            Hopper hopper =
                    (Hopper) world.getBlockAt(craftingTable.getLocation().add(1, 0, 0)).getState();
            if (hopperIsFacing(hopper, Direction.WEST)) {
                hoppersWhereItemsComesFrom.add(hopper);
            }
        }
        if (world.getBlockAt(craftingTable.getLocation().subtract(1, 0, 0)).getType() == Material.HOPPER) {
            Hopper hopper = (Hopper) world.getBlockAt(craftingTable.getLocation().subtract(1, 0,
                    0)).getState();
            if (hopperIsFacing(hopper, Direction.EAST)) {
                hoppersWhereItemsComesFrom.add(hopper);
            }
        }
        if (world.getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getType() == Material.HOPPER) {
            Hopper hopper =
                    (Hopper) world.getBlockAt(craftingTable.getLocation().add(0, 0, 1)).getState();
            if (hopperIsFacing(hopper, Direction.NORTH)) {
                hoppersWhereItemsComesFrom.add(hopper);
            }
        }
        if (world.getBlockAt(craftingTable.getLocation().subtract(0, 0, 1)).getType() == Material.HOPPER) {
            Hopper hopper = (Hopper) world.getBlockAt(craftingTable.getLocation().subtract(0, 0,
                    1)).getState();
            if (hopperIsFacing(hopper, Direction.SOUTH)) {
                hoppersWhereItemsComesFrom.add(hopper);
            }
        }
        return hoppersWhereItemsComesFrom;
    }

    /**
     * @return if the hopper faces to the direction it returns true, else false
     */

    private boolean hopperIsFacing(Hopper hopper, Direction direction) {
        if (direction == Direction.DOWN) {
            return hopper.getRawData() == 0 || hopper.getRawData() == 8;
        }
        if (direction == Direction.NORTH) {
            return hopper.getRawData() == 2 || hopper.getRawData() == 10;
        }
        if (direction == Direction.SOUTH) {
            return hopper.getRawData() == 3 || hopper.getRawData() == 11;
        }
        if (direction == Direction.WEST) {
            return hopper.getRawData() == 4 || hopper.getRawData() == 12;
        }
        if (direction == Direction.EAST) {
            return hopper.getRawData() == 5 || hopper.getRawData() == 13;
        }
        return false;
    }

    /**
     * Checks if a list of itemStacks contains at least one itemStack
     */

    private boolean itemStackListContainsAtLeast(ArrayList<ItemStack> itemStacks, ItemStack checkItemStack) {
        ItemStack testCheckItemStack = checkItemStack.clone();
        testCheckItemStack.setAmount(1);
        for (ItemStack itemStackInList: itemStacks) {
            ItemStack testItemStackInList = itemStackInList.clone();
            testItemStackInList.setAmount(1);
            if (!testCheckItemStack.isSimilar(testItemStackInList)) {
                continue;
            }
            if (itemStackInList.getAmount() >= checkItemStack.getAmount()) {
                return true;
            }
        }
        return false;
    }

}
