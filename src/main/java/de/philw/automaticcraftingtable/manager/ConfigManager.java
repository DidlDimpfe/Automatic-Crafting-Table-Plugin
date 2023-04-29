package de.philw.automaticcraftingtable.manager;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    /**
     * This method creates a config and saves the default values from it.
     */

    public static void setUpConfig(AutomaticCraftingTable automaticCraftingTable) {
        ConfigManager.config = automaticCraftingTable.getConfig();
        automaticCraftingTable.saveDefaultConfig();
    }

    public static String getCraftingTableDisplay() {
        return config.getString("crafting-table-ui-display");
    }

    public static boolean getEnabled() {
        return config.getBoolean("enabled");
    }

    public static String getSpaceDisplay() {
        return config.getString("crafting-table-ui-space-display");
    }

    public static int getTimer() {
        return config.getInt("crafting-table-timer");
    }
}
