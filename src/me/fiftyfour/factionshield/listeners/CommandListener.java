package me.fiftyfour.factionshield.listeners;

import com.massivecraft.factions.*;
import me.fiftyfour.factionshield.Main;
import me.fiftyfour.factionshield.items.GuiItems;
import me.fiftyfour.factionshield.objects.IconMenu;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommandListener implements Listener {
    private final List<String> alias;
    private final List<String> clearAlias;
    private final List<String> infoAlias;
    private final List<String> coreAlias;
    private final FileConfiguration config = Main.getPlugin().getConfig();

    public CommandListener() {
        this.alias = new ArrayList<>();
        this.infoAlias = new ArrayList<>();
        this.clearAlias = new ArrayList<>();
        this.coreAlias = new ArrayList<>();
        this.infoAlias.add("/f shield info");
        this.infoAlias.add("/f shields info");
        this.clearAlias.add("/f shield clear");
        this.clearAlias.add("/f shields clear");
        this.coreAlias.add("/f shield setcore");
        this.coreAlias.add("/f shields setcore");
        this.alias.add("/f shield");
        this.alias.add("/f shields");
    }

    @EventHandler
    public void onFactionCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        if (cmd.contains("info")) {
            if (!event.getPlayer().hasPermission("factionshield.info")) {
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return;
            }
            for (String alias : infoAlias) {
                if (cmd.startsWith(alias)) {
                    if (!FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction().isNormal()) {
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("lang.no-factions")));
                        event.setCancelled(true);
                        return;
                    }
                    if (!Board.getInstance().getFactionAt(new FLocation(event.getPlayer().getLocation())).isNormal()) {
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("lang.no-shield")));
                    } else {
                        StringBuilder hours = new StringBuilder();
                        for (Integer hour : Main.data.getIntegerList(Board.getInstance().getFactionAt(new FLocation(event.getPlayer().getLocation())).getId() + ".hours")) {
                            hours.append(", ").append(hour);
                        }
                        hours.delete(0, 1);
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("lang.command-info").replace("%hours%", hours.toString()).replace("%current%", "(Current hour: " + Main.getPlugin().calendar.get(Calendar.HOUR_OF_DAY) + ")")));
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (cmd.contains("clear") && event.getPlayer().hasPermission("factionshield.admin")) {
            for (String alias : clearAlias) {
                if (cmd.startsWith(alias)) {
                    String[] args = cmd.split(" ");
                    //f shield clear <faction>
                    if (args.length != 4){
                        if (!Board.getInstance().getFactionAt(new FLocation(event.getPlayer().getLocation())).isNormal()) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("lang.no-shield")));
                        } else {
                            Main.data.set(Board.getInstance().getFactionAt(new FLocation(event.getPlayer().getLocation())).getId(), null);
                            Main.getPlugin().saveData();
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("lang.shield-cleared")));
                        }
                    } else {
                        Faction faction = Factions.getInstance().getByTag(args[3]);
                        if (faction == null){
                            event.getPlayer().sendMessage(ChatColor.RED + "That is not a faction name!");
                        } else {
                            Main.data.set(faction.getId(), null);
                            Main.getPlugin().saveData();
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("lang.shield-cleared")));
                        }
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (cmd.contains("setcore")) {
            for (String alias : coreAlias) {
                if (cmd.startsWith(alias)) {
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                    Chunk chunk = event.getPlayer().getLocation().getChunk();
                    if (!event.getPlayer().hasPermission("factionshield.use") || fPlayer.getFaction().getFPlayerLeader() != fPlayer) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                        return;
                    }
                    if (Board.getInstance().getFactionAt(new FLocation(fPlayer.getPlayer().getLocation())) != fPlayer.getFaction()) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You must be in your own faction's land to do this!");
                        return;
                    }
                    if (Main.data.getIntegerList(fPlayer.getFactionId() + ".hours").isEmpty()) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You must set you faction shield first! (/f shield)");
                        return;
                    }
                    List<String> core = new ArrayList<>(Main.data.getStringList(fPlayer.getFactionId() + ".core"));
                    if (core.size() >= Main.getPlugin().getConfig().getInt("max-protected-chunks")){
                        event.getPlayer().sendMessage(ChatColor.RED + "You have set the maximum amount of protected chunks!");
                        return;
                    }
                    if (core.contains(chunk.getX() + ", " + chunk.getZ())){
                        event.getPlayer().sendMessage(ChatColor.RED + "This chunk is already protected by your shield!");
                        return;
                    } else {
                        core.add(chunk.getX() + ", " + chunk.getZ());
                        Main.data.set(fPlayer.getFactionId() + ".core", core);
                        event.getPlayer().sendMessage(ChatColor.GREEN + "This chunk is now protected by your shield!");
                        event.getPlayer().sendMessage(ChatColor.RED + "You can only protect " + (Main.getPlugin().getConfig().getInt("max-protected-chunks") - core.size()) + " more chunks with your shield!");
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        }else {
            for (String alias : alias) {
                if (cmd.startsWith(alias)) {
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                    if (!event.getPlayer().hasPermission("factionshield.use") || fPlayer.getFaction().getFPlayerLeader() != fPlayer) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                        return;
                    }
                    if (Board.getInstance().getFactionAt(new FLocation(fPlayer.getPlayer().getLocation())) != fPlayer.getFaction()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You must be in your own faction's land to do this!");
                        return;
                    }
                    if (Main.data.getIntegerList(FPlayers.getInstance().getByPlayer(event.getPlayer()).getFactionId() + ".hours").isEmpty())
                        openGui(event.getPlayer());
                    else
                        event.getPlayer().sendMessage(ChatColor.RED + "You have already set your faction's shield!");
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private void openGui(Player player) {
        IconMenu menu = new IconMenu(this.config.getInt("gui.rowsize"), ChatColor.translateAlternateColorCodes('&', this.config.getString("gui.title")));
        ItemStack fillMaterial = new ItemStack(this.config.getInt("gui.items.fill-material.id"), 1, (short) this.config.getInt("gui.items.fill-material.subid"));
        ItemMeta fillMeta = fillMaterial.getItemMeta();
        fillMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.config.getString("gui.items.fill-material.name")));
        fillMaterial.setItemMeta(fillMeta);

        for (int i = 0; i < 24; ++i) {
            if (Main.data.getIntegerList(FPlayers.getInstance().getByPlayer(player).getFactionId() + ".hours").contains(i)) {
                menu.addButton(this.config.getInt("gui.items." + i + ".slot"), GuiItems.getHourOnMaterial(i));
            } else {
                menu.addButton(this.config.getInt("gui.items." + i + ".slot"), GuiItems.getHourOffMaterial(i));
            }
        }

        menu.addButton(4, new ItemStack(Material.REDSTONE_BLOCK, 1), ChatColor.RED + "" + ChatColor.BOLD + "WARNING: THIS CAN ONLY BE SET ONCE!!", ChatColor.RED + "You will only get one chance to confirm this!");

        for (int i = 0; i < menu.getSize(); ++i) {
            if (menu.getItem(i) == null) {
                menu.addButton(i, fillMaterial);
            }
        }
        menu.setClick((clicker, menuClicked, slot, item) -> {
            if (item != null) {
                int hourStart = 0;
                boolean found = false;
                for (int i = 0; i < 24; ++i) {
                    if (this.config.getInt("gui.items." + i + ".slot") == slot) {
                        found = true;
                        hourStart = i;
                    }
                }
                if (found) {
                    openConfirmGUI(player, hourStart);
                }
            }
            return false;
        });
        menu.open(player);
    }


    public void openConfirmGUI(Player player, int hourStart) {
        final ItemStack deny = new ItemStack(Material.valueOf("WOOL"), 1, (short) 14);
        final ItemStack confirm = new ItemStack(Material.valueOf("WOOL"), 1, (short) 5);
        IconMenu menu = new IconMenu(3, ChatColor.RED + "Confirm Faction Shield", (clicker, menu1, slot, item) -> {
            if (clicker == player) {
                if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
                if (item.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "Confirm") && item.getType() == confirm.getType() && item.getDurability() == confirm.getDurability()) {
                    List<Integer> protectedHours = Main.data.getIntegerList(FPlayers.getInstance().getByPlayer(clicker).getFactionId() + ".hours");
                    for (int i = hourStart; protectedHours.size() < this.config.getInt("protected-hours"); i++) {
                        if (i >= 23) i = 0;
                        protectedHours.add(i);
                    }
                    Main.data.set(FPlayers.getInstance().getByPlayer(clicker).getFactionId() + ".hours", protectedHours);
                    Main.getPlugin().saveData();
                    clicker.sendMessage(ChatColor.GREEN + "Faction shield is now setup!");
                    clicker.sendMessage(ChatColor.RED + "Don't forget to do /f shield setcore on each chunk you want protected by the shield!!");
                    return true;
                } else if (item.getItemMeta().getDisplayName().contains(ChatColor.RED + "Deny") && item.getType() == deny.getType() && item.getDurability() == deny.getDurability()) {
                    clicker.sendMessage(ChatColor.RED + "Cancelling Faction shield setup!");
                    return true;
                }
            }
            return false;
        });
        menu.addButton(0, deny, ChatColor.RED + "Deny");
        menu.addButton(1, deny, ChatColor.RED + "Deny");
        menu.addButton(2, deny, ChatColor.RED + "Deny");

        menu.addButton(4, new ItemStack(Material.PAPER), ChatColor.RED + "WARNING!!!",
                ChatColor.WHITE + "Confirming this action will", ChatColor.WHITE + "set your faction shield to work for",
                ChatColor.WHITE + String.valueOf(this.config.getInt("protected-hours")) + " hours after " + hourStart + "00 (Current hour: " + Main.getPlugin().calendar.get(Calendar.HOUR_OF_DAY) + ").", ChatColor.WHITE + "This cannot be undone!!");

        menu.addButton(6, confirm, ChatColor.GREEN + "Confirm");
        menu.addButton(7, confirm, ChatColor.GREEN + "Confirm");
        menu.addButton(8, confirm, ChatColor.GREEN + "Confirm");

        menu.addButton(9, deny, ChatColor.RED + "Deny");
        menu.addButton(10, deny, ChatColor.RED + "Deny");
        menu.addButton(11, deny, ChatColor.RED + "Deny");
        menu.addButton(15, confirm, ChatColor.GREEN + "Confirm");
        menu.addButton(16, confirm, ChatColor.GREEN + "Confirm");
        menu.addButton(17, confirm, ChatColor.GREEN + "Confirm");


        menu.addButton(18, deny, ChatColor.RED + "Deny");
        menu.addButton(19, deny, ChatColor.RED + "Deny");
        menu.addButton(20, deny, ChatColor.RED + "Deny");
        menu.addButton(24, confirm, ChatColor.GREEN + "Confirm");
        menu.addButton(25, confirm, ChatColor.GREEN + "Confirm");
        menu.addButton(26, confirm, ChatColor.GREEN + "Confirm");
        menu.open(player);
    }
}