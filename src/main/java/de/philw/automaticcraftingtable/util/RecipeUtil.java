package de.philw.automaticcraftingtable.util;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class RecipeUtil {
    private ArrayList<Recipe> recipes;
    private final AutomaticCraftingTable automaticCraftingTable;
    private List<ItemStack> damagedIngredientList;
    private final Map<List<ItemStack>, ItemStack> cache = new HashMap<>();

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
            if (recipe.getResult().getType() == Material.FIREWORK_ROCKET)
                continue; // skip firework recipe with no NBT
            recipes.add(recipe);
        }
        recipes.addAll(getExtraRecipes());
        return recipes;
    }

    public List<Recipe> getExtraRecipes() {
        // This method is needed for recipes the recipe iterator from the server doesn't deliver

        List<Recipe> recipes = new ArrayList<>();

        recipes.add(getFireWorkRecipe(1));
        recipes.add(getFireWorkRecipe(2));
        recipes.add(getFireWorkRecipe(3));

        recipes.addAll(getShulkerBoxRecipes());
        return recipes;
    }

    private ShapelessRecipe getFireWorkRecipe(int power) {
        ItemStack rocket = new ItemStack(Material.FIREWORK_ROCKET, 3);
        FireworkMeta rocketMeta = (FireworkMeta) rocket.getItemMeta();
        assert rocketMeta != null;
        rocketMeta.setPower(power);
        rocket.setItemMeta(rocketMeta);
        ShapelessRecipe rocketRecipe = new ShapelessRecipe(new NamespacedKey(automaticCraftingTable, "de.philw.automaticcraftingtable.firework_rocket_" + power), rocket);
        rocketRecipe.addIngredient(Material.PAPER);
        rocketRecipe.addIngredient(power, Material.GUNPOWDER);
        return rocketRecipe;
    }

    private List<ShapelessRecipe> getShulkerBoxRecipes() {
        List<ShapelessRecipe> shulkerRecipes = new ArrayList<>();
        // Add all recipes for dyed shulker boxes to dyed shulker boxes
        for (DyeColor dyeColorFromBox: DyeColor.values()) {
            for (DyeColor dyeColorToBox: DyeColor.values()) {
                if (dyeColorFromBox == dyeColorToBox) {
                    continue;
                }
                ItemStack toBox = new ItemStack(Material.valueOf(dyeColorToBox + "_SHULKER_BOX"));
                ShapelessRecipe shulkerRecipe = new ShapelessRecipe(new NamespacedKey(
                        automaticCraftingTable,
                        "de.philw.automaticcraftingtable.shulker_box_from_" + dyeColorFromBox.toString().toLowerCase() + "_to_" + dyeColorToBox.toString().toLowerCase()),
                        toBox);
                shulkerRecipe.addIngredient(Material.valueOf(dyeColorToBox + "_DYE"));
                shulkerRecipe.addIngredient(Material.valueOf(dyeColorFromBox + "_SHULKER_BOX"));
                shulkerRecipes.add(shulkerRecipe);
            }
        }
        // Add all recipes for normal shulker to all dyed shulker boxes
        for (DyeColor dyeColorToBox: DyeColor.values()) {
            ItemStack toBox = new ItemStack(Material.valueOf(dyeColorToBox + "_SHULKER_BOX"));
            ShapelessRecipe shulkerRecipe = new ShapelessRecipe(new NamespacedKey(
                    automaticCraftingTable,
                    "de.philw.automaticcraftingtable.shulker_box_from_normal_to_" + dyeColorToBox.toString().toLowerCase()),
                    toBox);
            shulkerRecipe.addIngredient(Material.valueOf(dyeColorToBox + "_DYE"));
            shulkerRecipe.addIngredient(Material.SHULKER_BOX);
            shulkerRecipes.add(shulkerRecipe);
        }
        return shulkerRecipes;
    }

    /**
     * This method returns am Item from a crafting recipe or null
     *
     * @param items The items from the recipe
     */

    public ItemStack getCraftResult(List<ItemStack> items) {
        if (cache.containsKey(items)) {
            damagedIngredientList = items;
            // This is because we later need to get the
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
                    damagedIngredientList = ((ShapelessRecipe) recipe).getIngredientList();
                    return result;
                }
            } else if (recipe instanceof ShapedRecipe) { // Shaped recipe
                result = matchesShaped((ShapedRecipe) recipe, items) ? recipe.getResult() : null;
                if (result != null) {
                    cache.put(items, result);
                    damagedIngredientList = new ArrayList<>(((ShapedRecipe)recipe).getIngredientMap().values());
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
        return items.isEmpty();
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

//    private  HashMap<List<ItemStack>, ArrayList<ItemStack>> cacheIngredientLists = new HashMap<>();

    /**
     * This method returns the ingredients from the recipe in the workbench. Unnecessary items in the workbench will
     * be filtered.
     *
     * @param location The location from the workbench
     */

    public ArrayList<ItemStack> getIngredientList(Location location) {
        ArrayList<Integer> amountFromItem = new ArrayList<>();
        ArrayList<ItemStack> ingredientList = new ArrayList<>();
        for (ItemStack itemStack: Objects.requireNonNull(damagedIngredientList)) {
            if (itemStack == null) {
                continue;
            }
            amountFromItem.add(itemStack.getAmount());
        }
        int countedItems = 0;
        for (int index = 0; index < 9; index++) {
            ItemStack itemStack = automaticCraftingTable.getCraftingTableManager().getItemFromIndex(location, index);
            if (itemStack == null) {
                continue;
            }
            itemStack.setAmount(amountFromItem.get(countedItems));
            ingredientList.add(itemStack);
            countedItems++;
        }
        return StackItems.combine(ingredientList);
    }

    public Map<List<ItemStack>, ItemStack> getCache() {
        return cache;
    }

}