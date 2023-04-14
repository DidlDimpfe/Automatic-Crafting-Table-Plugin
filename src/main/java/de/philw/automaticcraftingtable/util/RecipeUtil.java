package de.philw.automaticcraftingtable.util;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
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

    public ArrayList<Recipe> getRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        Iterator<Recipe> it = automaticCraftingTable.getServer().recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            recipes.add(recipe);
        }
        return recipes;
    }

    private final Map<List<ItemStack>, ItemStack> cache = new HashMap<>();

    public ItemStack getCraftResult(List<ItemStack> items) {
        if (cache.containsKey(items))
            return cache.get(items);

        if (items.size() != 9) { // list correct?
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

        // Load recipes after every plugin activated.
        if (recipes == null) {
            recipes = getRecipes();
        }
        for (Recipe recipe : recipes) {
            if (recipe instanceof ShapelessRecipe) { // shapeless recipe
                result = matchesShapeless(((ShapelessRecipe) recipe).getChoiceList(), items) ? recipe.getResult()
                        : null;
                if (result != null) {
                    cache.put(items, result);
                    this.recipe = recipe;
                    return result;
                }
            } else if (recipe instanceof ShapedRecipe) { // shaped recipe
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

    private boolean matchesShapeless(List<RecipeChoice> choice, List<ItemStack> items) {
        items = new ArrayList<>(items);
        for (RecipeChoice c : choice) {
            boolean match = false;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item == null || item.getType() == Material.AIR)
                    continue;
                if (c.test(item)) {
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
        for (int i = 0; i < recipe.getShape().length; i++) {
            for (int j = 0; j < recipe.getShape()[i].length(); j++) {
                recipeArray[i][j] = recipe.getChoiceMap().get(recipe.getShape()[i].toCharArray()[j]);
            }
        }

        int counter = 0;
        ItemStack[][] itemsArray = new ItemStack[3][3];
        for (int i = 0; i < itemsArray.length; i++) {
            for (int j = 0; j < itemsArray[i].length; j++) {
                itemsArray[i][j] = items.get(counter);
                counter++;
            }
        }

        // itemsArray manipulation
        Object[][] tmpArray = reduceArray(itemsArray);
        itemsArray = new ItemStack[tmpArray.length][tmpArray[0].length];
        for (int i = 0; i < tmpArray.length; i++) {
            for (int j = 0; j < tmpArray[i].length; j++) {
                itemsArray[i][j] = (ItemStack) tmpArray[i][j];
            }
        }
        ItemStack[][] itemsArrayMirrored = new ItemStack[itemsArray.length][itemsArray[0].length];
        for (int i = 0; i < itemsArray.length; i++) {
            int jPos = 0;
            for (int j = itemsArray[i].length - 1; j >= 0; j--) {
                itemsArrayMirrored[i][jPos] = itemsArray[i][j];
                jPos++;
            }
        }
        return match(itemsArray, recipeArray) || match(itemsArrayMirrored, recipeArray);
    }

    private boolean match(ItemStack[][] itemsArray, RecipeChoice[][] recipeArray) {
        boolean match = true;
        if (itemsArray.length == recipeArray.length && itemsArray[0].length == recipeArray[0].length) {
            for (int i = 0; i < recipeArray.length; i++) {
                for (int j = 0; j < recipeArray[0].length; j++) {
                    if (recipeArray[i][j] != null && itemsArray[i][j] != null) {
                        if (!recipeArray[i][j].test(itemsArray[i][j])) {
                            match = false;
                            break;
                        }
                    } else if ((recipeArray[i][j] == null && itemsArray[i][j] != null)
                            || (recipeArray[i][j] != null && itemsArray[i][j] == null)) {
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

    public ArrayList<ItemStack> getIngredientList(Location location) {
        if (recipe instanceof ShapelessRecipe) {
            return (ArrayList<ItemStack>) ((ShapelessRecipe) recipe).getIngredientList();
        } else { // If it's ShapedRecipe
            if (cacheRecipe != null && cacheRecipe == recipe) {
                return cacheIngredientList;
            }
            ArrayList<ItemStack> ingredientList = new ArrayList<>();
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            for (var entry : shapedRecipe.getIngredientMap().entrySet()) {
                int index = letterToAlphabetPos(entry.getKey()) - 1;
                if (automaticCraftingTable.getCraftingTableManager().getItemFromIndex(location, index) != null) {
                    ItemStack itemStack = automaticCraftingTable.getCraftingTableManager().getItemFromIndex(location,
                            index);
                    itemStack.setAmount(entry.getValue().getAmount());
                    ingredientList.add(itemStack);
                }
            }
            cacheIngredientList = StackItems.combine(ingredientList);
            cacheRecipe = recipe;
            return cacheIngredientList;
        }
    }

    private int letterToAlphabetPos(char letter) {
        return Character.toUpperCase(letter) - 64;
    }
}