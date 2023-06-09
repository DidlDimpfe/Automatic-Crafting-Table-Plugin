package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.holder.RegisteredCraftingTableHolder;
import de.philw.automaticcraftingtable.holder.UsedRecipesHolder;
import de.philw.automaticcraftingtable.listener.BreakBlockListener;
import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableLeftClickListener;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.task.CheckHopperTask;
import de.philw.automaticcraftingtable.util.RecipeUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutomaticCraftingTable extends JavaPlugin {

    private CraftingTableManager craftingTableManager;
    private RecipeUtil recipeUtil;

    @Override
    public void onEnable() {
        new Metrics(this, 19035);

        ConfigManager.setUpConfig(this);
        if (!ConfigManager.getEnabled()) {
            System.out.println(getMessageBeginning() + "Plugin has not been enabled. Change it in config.yml!");
            return;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI")!= null) {
            new RegisteredCraftingTableHolder(this).register();
            new UsedRecipesHolder(this).register();
        }
        craftingTableManager = new CraftingTableManager(this);
        recipeUtil = new RecipeUtil(this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableLeftClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableEditUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlockListener(this), this);

        int timer = ConfigManager.getTimer();

        Bukkit.getScheduler().runTaskTimer(this, new CheckHopperTask(this), timer, timer);

        System.out.println(getMessageBeginning() + "Plugin has been enabled.");
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