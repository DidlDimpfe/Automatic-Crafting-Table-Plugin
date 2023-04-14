package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.listener.BreakBlockListener;
import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableRightClickListener;
import de.philw.automaticcraftingtable.listener.HopperListener;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AutomaticCraftingTable extends JavaPlugin {

    private CraftingTableManager craftingTableManager;
    private final Map<List<ItemStack>, ItemStack> cache = new HashMap<>();
    private ArrayList<Recipe> recipes;
    private RecipeUtil recipeUtil;

    @Override
    public void onEnable() {
        craftingTableManager = new CraftingTableManager(this);
        recipeUtil = new RecipeUtil(this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableRightClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableEditUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new HopperListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlockListener(this), this);
    }

    public CraftingTableManager getCraftingTableManager() {
        return craftingTableManager;
    }

    public RecipeUtil getRecipeUtil() {
        return recipeUtil;
    }
}
