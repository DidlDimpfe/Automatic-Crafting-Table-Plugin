package de.philw.automaticcraftingtable.holder;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UsedRecipesHolder extends PlaceholderExpansion {

    private final AutomaticCraftingTable automaticCraftingTable;

    public UsedRecipesHolder (AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @Override
    public @NotNull String getIdentifier() {
        return automaticCraftingTable.getDescription().getName().toLowerCase() + "UsedRecipes";
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
     * @return a String from all already automatic crafted items in this session.
     */

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        StringBuilder builder = new StringBuilder();
        List<ItemStack> used = new ArrayList<>();
        for (ItemStack itemStack: automaticCraftingTable.getRecipeUtil().getCache().values()) {
            if (!used.contains(itemStack)) {
                used.add(itemStack);
                builder.append(itemStack).append(", ");
            }
        }

        return builder.toString().isEmpty() ? "Nothing used yet." : builder.substring(0 , builder.toString().length() -2);
    }
}
