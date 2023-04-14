package de.philw.automaticcraftingtable.manager;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.util.ItemStackSerializer;
import de.philw.automaticcraftingtable.util.StackItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CraftingTableManager {

    private final AutomaticCraftingTable automaticCraftingTable;
    private final File file;
    private final YamlConfiguration craftingTables;

    public CraftingTableManager(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;

        if (!automaticCraftingTable.getDataFolder().exists()) {
            automaticCraftingTable.getDataFolder().mkdir();
        }

        this.file = new File(automaticCraftingTable.getDataFolder(), "craftingTables.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not load file craftingTables.yml");
            }
        }

        craftingTables = YamlConfiguration.loadConfiguration(file);
    }

    public void saveCraftingTables() {
        try {
            craftingTables.save(file);
        } catch (IOException e) {
            System.out.println("Could not save craftingTables.yml");
        }
    }

    public ItemStack getItemFromIndex(Location location, int index) {
        return craftingTables.getString(getSavedLocation(location) + "." + index).equals("null") ? null :
                ItemStackSerializer.deserialize(craftingTables.getString(getSavedLocation(location) + "." + index));
    }

    public void addItemToIndex(Location location, int index, ItemStack itemStack) {
        craftingTables.set(getSavedLocation(location) + "." + index, itemStack != null ?
                ItemStackSerializer.serialize(itemStack) : "null");
    }

    public void addEmptyCraftingTable(Location location) {

        for (int i = 0; i < 9; i++) {
            craftingTables.set(getSavedLocation(location) + "." + i, "null");
        }
    }

    public boolean isCraftingTableRegistered(Location location) {
        return craftingTables.getString(getSavedLocation(location) + ".0") != null;
    }

    public String getSavedLocation(Location location) {
        return location.getWorld().getName() + "," + (int) location.getX() + "," + (int) location.getY() + "," + (int) location.getZ();
    }

    public Location getLocationFromSavedString(String string) {
        String[] strings = string.split(",");
        World world = Bukkit.getWorld(strings[0]);
        int x = Integer.parseInt(strings[1]);
        int y = Integer.parseInt(strings[2]);
        int z = Integer.parseInt(strings[3]);
        return new Location(world, x, y, z);
    }

    public int castFromSmallInventoryToBigInventory(int i) {
        if (i >= 0 && i <= 2) {
            return i + 3;
        } else if (i >= 3 && i <= 5) {
            return i + 9;
        } else if (i >= 6 && i <= 8) {
            return i + 15;
        }
        return 0;
    }

    public int castFromBigInventoryToSmallInventory(int i) {
        if (i >= 3 && i <= 5) {
            return i - 3;
        } else if (i >= 12 && i <= 14) {
            return i - 9;
        } else if (i >= 21 && i <= 23) {
            return i - 15;
        }
        return 0;
    }

    public ArrayList<ItemStack> getItemsInCraftingTable(Location location) {
        ArrayList<ItemStack> itemsInCraftingTable = new ArrayList<>();
        for (int i = 0; i<9; i++) {
            itemsInCraftingTable.add(getItemFromIndex(location, i));
        }
        return StackItems.combine(itemsInCraftingTable);
    }

    public void removeWorkbench(Location location) {
        for (int i = 0; i<9; i++) {
            craftingTables.set(getSavedLocation(location) + "." + i, null);
        }
        craftingTables.set(getSavedLocation(location), null);
    }

}
