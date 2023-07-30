package de.philw.automaticcraftingtable.manager;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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

    public static String getCraftingTableInventoryDisplay() {
        return config.getString("crafting-table-ui-display");
    }

    public static boolean getEnabled() {
        return config.getBoolean("enabled");
    }

    public static int getTimer() {
        return config.getInt("crafting-table-timer");
    }

    public static boolean getVisualFeedbackEnabled() {
        return config.getBoolean("crafting-table-visual-feedback-enabled");
    }

    public static boolean getSoundFeedbackEnabled() {
        return config.getBoolean("crafting-table-sound-feedback-enabled");
    }

    public static boolean isSeparateFromOtherCraftingTables() {
        return config.getBoolean("separate-from-other-crafting-tables");
    }

    public static String getCraftingTableItemDisplay() {
        return config.getString("automatic-crafting-table-item-display");
    }

    public static ArrayList<String> getCraftingTableItemLore() {
        return (ArrayList<String>) config.getStringList("automatic-crafting-table-item-lore");
    }

    /**
     * This method adds a field, if the field is not in the config, to the config with the default value
     */

    private static void updateConfig(AutomaticCraftingTable automaticCraftingTable) {
        if (!config.isSet("enabled")) {
            config.set("enabled", Objects.requireNonNull(config.getDefaults()).get("enabled"));
        }
        if (!config.isSet("crafting-table-ui-display")) {
            config.set("crafting-table-ui-display", Objects.requireNonNull(config.getDefaults()).get("crafting-table-ui-display"));
        }
        if (!config.isSet("crafting-table-ui-space-display")) {
            config.set("crafting-table-ui-space-display", Objects.requireNonNull(config.getDefaults()).get("crafting-table-ui-space-display"));
        }
        if (!config.isSet("crafting-table-timer")) {
            config.set("crafting-table-timer", Objects.requireNonNull(config.getDefaults()).get("crafting-table-timer"));
        }
        if (!config.isSet("crafting-table-visual-feedback-enabled")) {
            config.set("crafting-table-visual-feedback-enabled", Objects.requireNonNull(config.getDefaults()).get("crafting-table-visual-feedback-enabled"));
        }
        if (!config.isSet("crafting-table-sound-feedback-enabled")) {
            config.set("crafting-table-sound-feedback-enabled", Objects.requireNonNull(config.getDefaults()).get("crafting-table-sound-feedback-enabled"));
        }
        if (!config.isSet("separate-from-other-crafting-tables")) {
            config.set("separate-from-other-crafting-tables", Objects.requireNonNull(config.getDefaults()).get("separate-from-other-crafting-tables"));
        }
        if (!config.isSet("automatic-crafting-table-item-display")) {
            config.set("automatic-crafting-table-item-display", Objects.requireNonNull(config.getDefaults()).get("automatic-crafting-table-item-display"));
        }
        if (!config.isSet("automatic-crafting-table-item-lore")) {
            config.set("automatic-crafting-table-item-lore", Objects.requireNonNull(config.getDefaults()).get("automatic-crafting-table-item-lore"));
        }
        try {
            config.save(new File(automaticCraftingTable.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}