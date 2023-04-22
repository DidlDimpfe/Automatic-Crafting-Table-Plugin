package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.holder.RegisteredCraftingTableHolder;
import de.philw.automaticcraftingtable.holder.UsedRecipesHolder;
import de.philw.automaticcraftingtable.listener.BreakBlockListener;
import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableRightClickListener;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.task.CheckHopperTask;
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
            System.out.println("[AutomaticCraftingTable] Plugin has not been enabled. Change it in config.yml");
            return;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI")!= null) {
            new RegisteredCraftingTableHolder(this).register();
            new UsedRecipesHolder(this).register();
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