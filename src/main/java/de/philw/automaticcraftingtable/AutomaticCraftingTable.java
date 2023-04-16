package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.listener.BreakBlockListener;
import de.philw.automaticcraftingtable.task.CheckHopperTask;
import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableRightClickListener;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutomaticCraftingTable extends JavaPlugin {

    private CraftingTableManager craftingTableManager;
    private RecipeUtil recipeUtil;

    @Override
    public void onEnable() {
        ConfigManager.setUpConfig(this);
        if (!ConfigManager.getEnabled()) {
            return;
        }

        craftingTableManager = new CraftingTableManager(this);
        recipeUtil = new RecipeUtil(this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableRightClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableEditUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlockListener(this), this);

        Bukkit.getScheduler().runTaskTimer(this, new CheckHopperTask(this), 4, 4);
    }

    public CraftingTableManager getCraftingTableManager() {
        return craftingTableManager;
    }

    public RecipeUtil getRecipeUtil() {
        return recipeUtil;
    }
}