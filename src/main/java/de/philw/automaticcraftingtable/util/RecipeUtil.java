package de.philw.automaticcraftingtable.util;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.*;

public class RecipeUtil {
    private ArrayList<Recipe> recipes;
    private final AutomaticCraftingTable automaticCraftingTable;
    private Recipe recipe;

    public RecipeUtil(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method returns all registered recipes.
     */

    public ArrayList<Recipe> getRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        Iterator<Recipe> recipeIterator = automaticCraftingTable.getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            recipes.add(recipe);
        }
        return recipes;
    }

    private final Map<List<ItemStack>, ItemStack> cache = new HashMap<>();

    /**
     * This method returns am Item from a crafting recipe or null
     *
     * @param items The items from the recipe
     */

    public ItemStack getCraftResult(List<ItemStack> items) {
        if (cache.containsKey(items)) {
            recipe = Bukkit.getRecipesFor(cache.get(items)).get(0); // This is because we later need to get the
            // recipe for the item but when a
            // false recipe is in cache for example because another crafting table the recipe still gets updated
            return cache.get(items);
        }

        if (items.size() != 9) { // is this list correct?
            return null;
        }
        boolean notNull = false;
        for (ItemStack itemstack : items) {
            if (itemstack != null) {
                notNull = true;
                break;
            }
        }
        if (!notNull) {
            return null;
        }

        ItemStack result;

        // This is because it loads AFTER every plugin has loaded for custom recipes
        if (recipes == null) {
            recipes = getRecipes();
        }
        for (Recipe recipe : recipes) {
            if (recipe instanceof ShapelessRecipe) { // Shapeless recipe
                result = matchesShapeless(((ShapelessRecipe) recipe).getChoiceList(), items) ? recipe.getResult()
                        : null;
                if (result != null) {
                    cache.put(items, result);
                    this.recipe = recipe;
                    return result;
                }
            } else if (recipe instanceof ShapedRecipe) { // Shaped recipe
                result = matchesShaped((ShapedRecipe) recipe, items) ? recipe.getResult() : null;
                if (result != null) {
                    cache.put(items, result);
                    this.recipe = recipe;
                    return result;
                }
            }
        }
        return null;
    }

    private boolean matchesShapeless(List<RecipeChoice> choices, List<ItemStack> items) {
        items = new ArrayList<>(items);
        for (RecipeChoice recipeChoice : choices) {
            boolean match = false;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item == null || item.getType() == Material.AIR)
                    continue;
                if (recipeChoice.test(item)) {
                    match = true;
                    items.remove(item);
                    break;
                }
            }
            if (!match)
                return false;
        }
        items.removeAll(Arrays.asList(null, new ItemStack(Material.AIR)));
        return items.size() == 0;
    }

    private boolean matchesShaped(ShapedRecipe recipe, List<ItemStack> items) {
        RecipeChoice[][] recipeArray = new RecipeChoice[recipe.getShape().length][recipe.getShape()[0].length()];
        for (int x = 0; x < recipe.getShape().length; x++) {
            for (int y = 0; y < recipe.getShape()[x].length(); y++) {
                recipeArray[x][y] = recipe.getChoiceMap().get(recipe.getShape()[x].toCharArray()[y]);
            }
        }

        int counter = 0;
        ItemStack[][] itemsArray = new ItemStack[3][3];
        for (int x = 0; x < itemsArray.length; x++) {
            for (int y = 0; y < itemsArray[x].length; y++) {
                itemsArray[x][y] = items.get(counter);
                counter++;
            }
        }

        Object[][] tmpArray = reduceArray(itemsArray);
        itemsArray = new ItemStack[tmpArray.length][tmpArray[0].length];
        for (int x = 0; x < tmpArray.length; x++) {
            for (int y = 0; y < tmpArray[x].length; y++) {
                itemsArray[x][y] = (ItemStack) tmpArray[x][y];
            }
        }

        ItemStack[][] itemsArrayMirrored = new ItemStack[itemsArray.length][itemsArray[0].length];
        for (int x = 0; x < itemsArray.length; x++) {
            int yPos = 0;
            for (int y = itemsArray[x].length - 1; y >= 0; y--) {
                itemsArrayMirrored[x][yPos] = itemsArray[x][y];
                yPos++;
            }
        }

        return match(itemsArray, recipeArray) || match(itemsArrayMirrored, recipeArray);
    }

    private boolean match(ItemStack[][] itemsArray, RecipeChoice[][] recipeArray) {
        boolean match = true;
        if (itemsArray.length == recipeArray.length && itemsArray[0].length == recipeArray[0].length) {
            for (int x = 0; x < recipeArray.length; x++) {
                for (int y = 0; y < recipeArray[0].length; y++) {
                    if (recipeArray[x][y] != null && itemsArray[x][y] != null) {
                        if (!recipeArray[x][y].test(itemsArray[x][y])) {
                            match = false;
                            break;
                        }
                    } else if ((recipeArray[x][y] == null && itemsArray[x][y] != null)
                            || (recipeArray[x][y] != null && itemsArray[x][y] == null)) {
                        match = false;
                        break;
                    }
                }
            }
            return match;
        }
        return false;
    }

    private static Object[][] reduceArray(Object[][] array) {
        ArrayList<Pos> positions = new ArrayList<>();
        for (int y = 0; y < array.length; y++)
            for (int x = 0; x < array[y].length; x++) {
                if (array[y][x] != null)
                    positions.add(new Pos(x, y));
            }

        Pos upperLeft = new Pos(array.length - 1, array[0].length - 1);
        Pos lowerRight = new Pos(0, 0);
        for (Pos pos : positions) {
            if (pos.y < upperLeft.y)
                upperLeft.y = pos.y;
            if (pos.x < upperLeft.x)
                upperLeft.x = pos.x;
            if (pos.y > lowerRight.y)
                lowerRight.y = pos.y;
            if (pos.x > lowerRight.x)
                lowerRight.x = pos.x;
        }
        Object[][] clean = new Object[(lowerRight.y - upperLeft.y) + 1][(lowerRight.x - upperLeft.x) + 1];
        int cleanY = 0;
        for (int y = upperLeft.y; y < lowerRight.y + 1; y++) {
            int cleanX = 0;
            for (int x = upperLeft.x; x < lowerRight.x + 1; x++) {
                clean[cleanY][cleanX] = array[y][x];
                cleanX++;
            }
            cleanY++;
        }
        return clean;
    }

    private Recipe cacheRecipe;
    private ArrayList<ItemStack> cacheIngredientList;

    /**
     * This method returns the ingredients from the recipe in the workbench. Unnecessary items in the workbench will
     * be filtered.
     *
     * @param location The location from the workbench
     */

    public ArrayList<ItemStack> getIngredientList(Location location) {
        if (recipe instanceof ShapelessRecipe) {
            return (ArrayList<ItemStack>) ((ShapelessRecipe) recipe).getIngredientList();
        } else { // If it's ShapedRecipe
            if (cacheRecipe != null && cacheRecipe == recipe) {
                return cacheIngredientList;
            }
            ArrayList<ItemStack> ingredientList = new ArrayList<>();
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
            int itemInRecipe = 1;
            for (int index = 0; index < 9; index++) {
                ItemStack itemStack = automaticCraftingTable.getCraftingTableManager().getItemFromIndex(location,
                        index);
                if (itemStack == null) {
                    continue;
                }
                // This is for recipes like craftingTables
                ItemStack realAmountItemStack = ingredientMap.get(Objects.requireNonNull(getCharForNumber(itemInRecipe)).toLowerCase().toCharArray()[0]);
                if (realAmountItemStack != null) {
                    itemStack.setAmount(realAmountItemStack.getAmount());
                } else {
                    // This is for recipes like furnace or chest or similar
                    itemStack.setAmount(ingredientMap.get(
                            Objects.requireNonNull(getCharForNumber(index + 1)).toLowerCase().toCharArray()[0]).getAmount());
                }
                itemInRecipe++;
                ingredientList.add(itemStack);
            }
            cacheIngredientList = StackItems.combine(ingredientList);
            cacheRecipe = recipe;
            return cacheIngredientList;
        }
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
    }

    public Map<List<ItemStack>, ItemStack> getCache() {
        return cache;
    }
}