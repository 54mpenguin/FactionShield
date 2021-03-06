package me.fiftyfour.factionshield.objects;

import me.fiftyfour.factionshield.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.createInventory;

public class IconMenu implements Listener {

    private final List<String> viewing = new ArrayList<>();
    private String name;
    private int size;
    private onClick click;
    private ItemStack[] items;
    private Inventory menu = null;

    public IconMenu(int size) {
        this.size = size * 9;
        this.items = new ItemStack[this.size];
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    public IconMenu(int size, String name) {
        this.size = size * 9;
        this.name = name;
        this.items = new ItemStack[this.size];
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    public IconMenu(int size, String name, onClick onClick) {
        this.size = size * 9;
        this.name = name;
        this.items = new ItemStack[this.size];
        this.click = onClick;
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    public void setSize(int size) {
        this.size = size * 9;
        items = Arrays.copyOf(items, this.size);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClick(onClick onClick) {
        if (!onClick.equals(this.click))
            this.menu = null;
        this.click = onClick;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : this.getViewers())
            close(p);
    }

    public void open(Player p) {
        if (this.menu == null || this.menu.getSize() != this.size || !this.menu.getTitle().equals(this.name))
            this.menu = createInventory(null, this.size, this.name);
        this.menu.setContents(items);
        p.openInventory(this.menu);
        viewing.add(p.getUniqueId().toString());
    }

    public void close(Player p) {
        if (p.getOpenInventory().getTitle().equals(name))
            p.closeInventory();
    }

    private List<Player> getViewers() {
        List<Player> viewers = new ArrayList<>();
        for (String s : viewing)
            viewers.add(Bukkit.getPlayer(s));
        return viewers;
    }

    public int getSize() {
        return size;
    }

    public ItemStack getItem(int position) {
        return items[position];
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (viewing.contains(event.getWhoClicked().getUniqueId().toString())) {
            Player p = (Player) event.getWhoClicked();
            if (event.getClickedInventory() != null && event.getCurrentItem() != null && Arrays.equals(event.getClickedInventory().getContents(), this.items)) {
                event.setCancelled(true);
                if (click.click(p, this, event.getSlot(), event.getCurrentItem()))
                    close(p);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewing.contains(event.getPlayer().getUniqueId().toString()))
            viewing.remove(event.getPlayer().getUniqueId().toString());
    }

    public void addButton(int position, ItemStack item, String name, String... lore) {
        items[position] = getItem(item, name, lore);
    }
    public void addButton(int position, ItemStack item) {
        items[position] = item;
    }

    private ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    public interface onClick {
        boolean click(Player clicker, IconMenu menu, int slot, ItemStack item);
    }
}
