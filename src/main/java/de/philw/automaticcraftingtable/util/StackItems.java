package de.philw.automaticcraftingtable.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StackItems {

    public static ArrayList<ItemStack> combine(List<ItemStack> items) {
        ArrayList<ItemStack> sorted = new ArrayList<>();
        for (ItemStack item : items) {
            if (item == null) continue;
            boolean putInPlace = false;
            for (ItemStack itemStack : sorted) {
                if (item.isSimilar(itemStack)) {
                    if (item.getAmount() + itemStack.getAmount() < itemStack.getMaxStackSize()) {
                        itemStack.setAmount(itemStack.getAmount() + item.getAmount());
                        putInPlace = true;
                    } else {
                        item.setAmount(item.getAmount() - (itemStack.getMaxStackSize() - itemStack.getAmount()));
                        itemStack.setAmount(itemStack.getMaxStackSize());
                    }
                    break;
                }
            }
            if (!putInPlace) {
                sorted.add(item);
            }
        }
        return sorted;
    }

}
