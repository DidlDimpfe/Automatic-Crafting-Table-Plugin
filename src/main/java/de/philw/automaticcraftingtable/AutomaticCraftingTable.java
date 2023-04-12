package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableRightClickListener;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutomaticCraftingTable extends JavaPlugin {

    private CraftingTableManager craftingTableManager;

    @Override
    public void onEnable() {
        craftingTableManager = new CraftingTableManager(this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableRightClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableEditUIListener(this), this);
    }

    public CraftingTableManager getCraftingTableManager() {
        return craftingTableManager;
    }
}
