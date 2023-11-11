package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.commands.GetACTCommand;
import de.philw.automaticcraftingtable.holder.RegisteredCraftingTableHolder;
import de.philw.automaticcraftingtable.holder.UsedRecipesHolder;
import de.philw.automaticcraftingtable.listener.BreakBlockListener;
import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableLeftClickListener;
import de.philw.automaticcraftingtable.listener.PlaceACTListener;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.task.CheckHopperTask;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import de.philw.automaticcraftingtable.util.RecipeUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class AutomaticCraftingTable extends JavaPlugin {

    private CraftingTableManager craftingTableManager;
    private RecipeUtil recipeUtil;

    @Override
    public void onEnable() {
        new Metrics(this, 19035);

        ConfigManager.setUpConfig(this);
        if (!ConfigManager.getEnabled()) {
            Bukkit.getServer().getLogger().warning(getMessageBeginning() + "Plugin has not been enabled. Change it it config.yml if you want!");
            return;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI")!= null) {
            new RegisteredCraftingTableHolder(this).register();
            new UsedRecipesHolder(this).register();
        }
        if (ConfigManager.isSeparateFromOtherCraftingTables()) {
            ACTBlockUTIL.automaticCraftingTable = this;
            Bukkit.getPluginManager().registerEvents(new PlaceACTListener(this), this);
            Objects.requireNonNull(getCommand("getACT")).setExecutor(new GetACTCommand(this));
        }
        try {
            craftingTableManager = new CraftingTableManager(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recipeUtil = new RecipeUtil(this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableLeftClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableEditUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlockListener(this), this);

        int timer = ConfigManager.getTimer();

        Bukkit.getScheduler().runTaskTimer(this, new CheckHopperTask(this), timer, timer);

        Bukkit.getServer().getLogger().info(getMessageBeginning() + "Plugin has been enabled.");

        if (ConfigManager.isSeparateFromOtherCraftingTables()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                craftingTableManager.registerAutomaticCraftingTables();
            });
        }
    }

    @Override
    public void onDisable() {
        craftingTableManager.unregisterAutomaticCraftingTables();
    }

    public CraftingTableManager getCraftingTableManager() {
        return craftingTableManager;
    }

    public RecipeUtil getRecipeUtil() {
        return recipeUtil;
    }

    public static String getMessageBeginning() {
        return "[AutomaticCraftingTable] ";
    }

}