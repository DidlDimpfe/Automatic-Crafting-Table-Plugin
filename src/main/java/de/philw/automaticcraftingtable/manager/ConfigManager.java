package de.philw.automaticcraftingtable.manager;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private static FileConfiguration config;


    /**
     * This method creates a config and saves the default values from it.
     */

    public static void setUpConfig(AutomaticCraftingTable automaticCraftingTable) {
        ConfigManager.config = automaticCraftingTable.getConfig();
        automaticCraftingTable.saveDefaultConfig();
        updateConfig(automaticCraftingTable);
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

    public static boolean getUINeedPermission() {
        return config.getBoolean("crafting-table-ui-need-permission");
    }

    /**
     * This method adds a field, if the field is not in the config, to the config with the default value
     */

    private static void updateConfig(AutomaticCraftingTable automaticCraftingTable) {
        if (!config.isSet("enabled")) {
            config.set("enabled", config.getDefaults().get("enabled"));
        }
        if (!config.isSet("crafting-table-ui-display")) {
            config.set("crafting-table-ui-display", config.getDefaults().get("crafting-table-ui-display"));
        }
        if (!config.isSet("crafting-table-ui-space-display")) {
            config.set("crafting-table-ui-space-display", config.getDefaults().get("crafting-table-ui-space-display"));
        }
        if (!config.isSet("crafting-table-timer")) {
            config.set("crafting-table-timer", config.getDefaults().get("crafting-table-timer"));
        }
        if (!config.isSet("crafting-table-ui-need-permission")) {
            config.set("crafting-table-ui-need-permission", config.getDefaults().get("crafting-table-ui-need-permission"));
        }
        try {
            config.save(new File(automaticCraftingTable.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}