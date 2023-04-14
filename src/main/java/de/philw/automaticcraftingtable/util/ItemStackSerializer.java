package de.philw.automaticcraftingtable.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ItemStackSerializer {
    public static String serialize(ItemStack item) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getType());
        if (item.getDurability() != 0) builder.append(":").append(item.getDurability());
        builder.append(" ").append(item.getAmount());
        for (Enchantment enchant : item.getEnchantments().keySet())
            builder.append(" ").append(enchant.getName()).append(":").append(item.getEnchantments().get(enchant));
        String name = getName(item);
        if (name != null) builder.append(" name:").append(name);
        String lore = getLore(item);
        if (lore != null) builder.append(" lore:").append(lore);
        Color color = getArmorColor(item);
        if (color != null) builder.append(" rgb:").append(color.getRed()).append("|").append(color.getGreen()).append(
                "|").append(color.getBlue());
        String owner = getOwner(item);
        if (owner != null) builder.append(" owner:").append(owner);
        return builder.toString();
    }

    public static ItemStack deserialize(String serializedItem) {
        String[] strings = serializedItem.split(" ");
        Map<Enchantment, Integer> enchants = new HashMap<>();
        String[] args;
        ItemStack item = new ItemStack(Material.AIR);
        for (String str : strings) {
            args = str.split(":");
            if (Material.matchMaterial(args[0]) != null && item.getType() == Material.AIR) {
                item.setType(Objects.requireNonNull(Material.matchMaterial(args[0])));
                if (args.length == 2) item.setDurability(Short.parseShort(args[1]));
                break;
            }
        }
        if (item.getType() == Material.AIR) {
            Bukkit.getLogger().info("Could not find a valid material for the item in \"" + serializedItem + "\"");
            return null;
        }
        for (String str : strings) {
            args = str.split(":", 2);
            if (isNumber(args[0])) item.setAmount(Integer.parseInt(args[0]));
            if (args.length == 1) continue;
            if (args[0].equalsIgnoreCase("name")) {
                setName(item, ChatColor.translateAlternateColorCodes('&', args[1]));
                continue;
            }
            if (args[0].equalsIgnoreCase("lore")) {
                setLore(item, ChatColor.translateAlternateColorCodes('&', args[1]));
                continue;
            }
            if (args[0].equalsIgnoreCase("rgb")) {
                setArmorColor(item, args[1]);
                continue;
            }
            if (args[0].equalsIgnoreCase("owner")) {
                setOwner(item, args[1]);
                continue;
            }
            if (Enchantment.getByName(args[0].toUpperCase()) != null) {
                enchants.put(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
            }
        }
        item.addUnsafeEnchantments(enchants);
        return item.getType().equals(Material.AIR) ? null : item;
    }

    private static String getOwner(ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta)) return null;
        return ((SkullMeta) item.getItemMeta()).getOwner();
    }

    private static void setOwner(ItemStack item, String owner) {
        try {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            Objects.requireNonNull(meta).setOwner(owner);
            item.setItemMeta(meta);
        } catch (Exception ignored) {
        }
    }

    private static String getName(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        if (!Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) return null;
        return item.getItemMeta().getDisplayName().replace(" ", "_").replace(ChatColor.COLOR_CHAR, '&');
    }

    private static void setName(ItemStack item, String name) {
        name = name.replace("_", " ");
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(name);
        item.setItemMeta(meta);
    }

    private static String getLore(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        if (!Objects.requireNonNull(item.getItemMeta()).hasLore()) return null;
        StringBuilder builder = new StringBuilder();
        List<String> lore = item.getItemMeta().getLore();
        for (int ind = 0; ind < Objects.requireNonNull(lore).size(); ind++) {
            builder.append(ind > 0 ? "|" : "").append(lore.get(ind).replace(" ", "_").replace(ChatColor.COLOR_CHAR,
                    '&'));
        }
        return builder.toString();
    }

    private static void setLore(ItemStack item, String lore) {
        lore = lore.replace("_", " ");
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setLore(Arrays.asList(lore.split("\\|")));
        item.setItemMeta(meta);
    }

    private static Color getArmorColor(ItemStack item) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta)) return null;
        return ((LeatherArmorMeta) item.getItemMeta()).getColor();
    }

    private static void setArmorColor(ItemStack item, String str) {
        try {
            String[] colors = str.split("\\|");
            int red = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue = Integer.parseInt(colors[2]);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            Objects.requireNonNull(meta).setColor(Color.fromRGB(red, green, blue));
            item.setItemMeta(meta);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }
}
