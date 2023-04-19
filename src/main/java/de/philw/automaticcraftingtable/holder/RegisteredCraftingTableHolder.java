package de.philw.automaticcraftingtable.holder;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegisteredCraftingTableHolder extends PlaceholderExpansion {

    private AutomaticCraftingTable automaticCraftingTable;

    public RegisteredCraftingTableHolder (AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @Override
    public @NotNull String getIdentifier() {
        return automaticCraftingTable.getDescription().getName().toLowerCase() + "Tables";
    }

    @Override
    public @NotNull String getAuthor() {
        return automaticCraftingTable.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return automaticCraftingTable.getDescription().getVersion();
    }

    /**
     * Note: a crafting table is only registered, if it's holding an item in it.
     * @return a number of registered craftingTables as String.
     */

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return automaticCraftingTable.getCraftingTableManager().getLocations().size() + "";
    }
}
