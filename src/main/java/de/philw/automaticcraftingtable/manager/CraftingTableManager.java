package de.philw.automaticcraftingtable.manager;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.util.ItemStackSerializer;
import de.philw.automaticcraftingtable.util.StackItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("ALL")
public class CraftingTableManager {

    private final File file;
    private final YamlConfiguration craftingTables;

    /**
     * The constructor of the class will connect to craftingTables.yml or create it.
     */

    public CraftingTableManager(AutomaticCraftingTable automaticCraftingTable) {

        if (!automaticCraftingTable.getDataFolder().exists()) {
            automaticCraftingTable.getDataFolder().mkdir();
        }

        this.file = new File(automaticCraftingTable.getDataFolder(), "craftingTables.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println(AutomaticCraftingTable.getMessageBeginning() + "Could not load file craftingTables.yml");
            }
        }

        craftingTables = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * This method saves all changes on craftingTables to the file
     */

    public void saveCraftingTables() {
        try {
            craftingTables.save(file);
        } catch (IOException e) {
            System.err.println(AutomaticCraftingTable.getMessageBeginning() + "Could not save craftingTables.yml");
        }
    }

    /**
     * This method gets you the Item from a specific craftingTable at a specific index
     *
     * @param location The location of which crafting table you want the info from
     * @param index    The index of the item you want to have (0-8)
     * @return The item from the workbench at the location and at the index from the index
     */

    public ItemStack getItemFromIndex(Location location, int index) {
        return Objects.equals(craftingTables.getString(getSavedLocation(location) + "." + index), "null") ? null :
                ItemStackSerializer.deserialize(Objects.requireNonNull(craftingTables.getString(getSavedLocation(location) + "." + index)));
    }

    /**
     * This method set the item from a specific craftingTable at a specific index to the wanted item.
     *
     * @param location  The location of which crafting table you want to change from
     * @param index     The index of the item you want to set (0-8)
     * @param itemStack The item you want to set
     */

    public void setItemToIndex(Location location, int index, ItemStack itemStack) {
        craftingTables.set(getSavedLocation(location) + "." + index, itemStack != null ?
                ItemStackSerializer.serialize(itemStack) : "null");
    }

    /**
     * This method adds a crafting table to the craftingTables.yml file.
     *
     * @param location The location where the crafting table is
     */

    public void addEmptyCraftingTable(Location location) {
        for (int i = 0; i < 9; i++) {
            craftingTables.set(getSavedLocation(location) + "." + i, "null");
        }
    }

    /**
     * This method return true if a craftingTable is registered and false if it is not.
     *
     * @param location The location where the crafting table is
     */

    public boolean isCraftingTableRegistered(Location location) {
        return craftingTables.getString(getSavedLocation(location) + ".0") != null;
    }

    /**
     * This method converts a location to a String how the location is stored in the craftingTables.yml file.
     *
     * @param location The location you want to convert
     */
    public String getSavedLocation(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," + (int) location.getX() + "," + (int) location.getY() + "," + (int) location.getZ();
    }

    /**
     * This method converts a String how the location is stored in the craftingTables.yml file to a real location.
     *
     * @param string The string you want to convert
     */

    public Location getLocationFromSavedString(String string) {
        String[] info = string.split(",");
        World world = Bukkit.getWorld(info[0]);
        int x = Integer.parseInt(info[1]);
        int y = Integer.parseInt(info[2]);
        int z = Integer.parseInt(info[3]);
        return new Location(world, x, y, z);
    }

    /**
     * This method returns all Items stacked from a crafting table.
     *
     * @param location The location where the craftingTable is.
     */

    public ArrayList<ItemStack> getItemsInCraftingTable(Location location) {
        ArrayList<ItemStack> itemsInCraftingTable = new ArrayList<>();
        for (int index = 0; index < 9; index++) {
            itemsInCraftingTable.add(getItemFromIndex(location, index));
        }
        return StackItems.combine(itemsInCraftingTable);
    }

    /**
     * This method removes all the info from a crafting in the craftingTables.yml
     *
     */

    public void removeCraftingTable(Location location) {
        for (int index = 0; index < 9; index++) {
            craftingTables.set(getSavedLocation(location) + "." + index, null);
        }
        craftingTables.set(getSavedLocation(location), null);
    }

    /**
     * This method returns all locations from registered crafting tables.
     */

    public Set<String> getLocations() {
        return Objects.requireNonNull(craftingTables.getConfigurationSection("")).getKeys(false);
    }

}
