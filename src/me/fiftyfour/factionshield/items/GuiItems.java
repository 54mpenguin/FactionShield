package me.fiftyfour.factionshield.items;

import me.fiftyfour.factionshield.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GuiItems {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static ItemStack getHourOnMaterial(int hour) {
        ItemStack hourOnMaterial = new ItemStack(config.getInt("gui.items.protected-material.id"), 1, (short)config.getInt("gui.items.protected-material.subid"));
        ItemMeta hourOnMeta = hourOnMaterial.getItemMeta();
        ArrayList<String> hourOnLore = new ArrayList<>();

        for (String line : config.getStringList("gui.items.protected-material.lore")) {
            hourOnLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        hourOnMeta.setLore(hourOnLore);
        hourOnMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("gui.items.protected-material.name").replace("%hour%", String.valueOf(hour))));
        hourOnMaterial.setItemMeta(hourOnMeta);
        return hourOnMaterial;
    }

    public static ItemStack getHourOffMaterial(int hour) {
        ItemStack hourOffMaterial = new ItemStack(config.getInt("gui.items.not-protected-material.id"), 1, (short)config.getInt("gui.items.not-protected-material.subid"));
        ItemMeta hourOffMeta = hourOffMaterial.getItemMeta();
        ArrayList<String> hourOffLore = new ArrayList<>();

        for (String line : config.getStringList("gui.items.not-protected-material.lore")) {
            hourOffLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        hourOffMeta.setLore(hourOffLore);
        hourOffMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("gui.items.not-protected-material.name").replace("%hour%", String.valueOf(hour))));
        hourOffMaterial.setItemMeta(hourOffMeta);
        return hourOffMaterial;
    }
}
