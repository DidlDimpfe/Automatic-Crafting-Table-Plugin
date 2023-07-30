package de.philw.automaticcraftingtable.util;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ACTBlockUTIL {

    public static AutomaticCraftingTable automaticCraftingTable;
    public static String metadataKey = "ACT";
    public static MetadataValue metadataValue = new MetadataValue() {
        @Nullable
        @Override
        public Object value() {
            return null;
        }

        @Override
        public int asInt() {
            return 0;
        }

        @Override
        public float asFloat() {
            return 0;
        }

        @Override
        public double asDouble() {
            return 0;
        }

        @Override
        public long asLong() {
            return 0;
        }

        @Override
        public short asShort() {
            return 0;
        }

        @Override
        public byte asByte() {
            return 0;
        }

        @Override
        public boolean asBoolean() {
            return false;
        }

        @NotNull
        @Override
        public String asString() {
            return "";
        }

        @Nullable
        @Override
        public Plugin getOwningPlugin() {
            return automaticCraftingTable;
        }

        @Override
        public void invalidate() {

        }
    };

    public static ItemStack getACT() {
        ItemStack act = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta itemMeta = act.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getCraftingTableItemDisplay()));
        itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = ConfigManager.getCraftingTableItemLore();
        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
        itemMeta.setLore(lore);
        act.setItemMeta(itemMeta);
        return act;
    }

}
