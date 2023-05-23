package de.philw.automaticcraftingtable.manager;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
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

    public static String getCraftingTableDisplay() {
        return config.getString("crafting-table-ui-display");
    }

    public static boolean getEnabled() {
        return config.getBoolean("enabled");
    }

    public static int getTimer() {
        return config.getInt("crafting-table-timer");
    }

    public static boolean getUINeedPermission() {
        return config.getBoolean("crafting-table-ui-need-permission");
    }

    public static boolean getVisualFeedbackEnabled() {
        return config.getBoolean("crafting-table-visual-feedback-enabled");
    }

    public static boolean getSoundFeedbackEnabled() {
        return config.getBoolean("crafting-table-sound-feedback-enabled");
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
        if (!config.isSet("crafting-table-ui-need-permission")) {
            config.set("crafting-table-ui-need-permission", Objects.requireNonNull(config.getDefaults()).get("crafting-table-ui-need-permission"));
        }
        if (!config.isSet("crafting-table-visual-feedback-enabled")) {
            config.set("crafting-table-visual-feedback-enabled", Objects.requireNonNull(config.getDefaults()).get("crafting-table-visual-feedback-enabled"));
        }
        if (!config.isSet("crafting-table-sound-feedback-enabled")) {
            config.set("crafting-table-sound-feedback-enabled", Objects.requireNonNull(config.getDefaults()).get("crafting-table-sound-feedback-enabled"));
        }
        try {
            config.save(new File(automaticCraftingTable.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}