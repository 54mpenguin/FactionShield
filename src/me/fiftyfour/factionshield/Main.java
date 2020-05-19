package me.fiftyfour.factionshield;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import me.fiftyfour.factionshield.listeners.CommandListener;
import me.fiftyfour.factionshield.listeners.ExplosionListener;
import me.fiftyfour.factionshield.listeners.FactionDeleteListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {
    private static Main plugin;
    public static FileConfiguration data;
    private File dataFile;
    public Calendar calendar;

    public static Main getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        calendar = Calendar.getInstance(TimeZone.getTimeZone(getConfig().getString("time-zone-to-use").toUpperCase()));
        calendar.set(Calendar.HOUR_OF_DAY, new Date().getHours());
        saveDefaultConfig();
        loadConfigs();
        Bukkit.getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FactionDeleteListener(), this);
    }

    public void onDisable() {
        try {
            data.save(this.dataFile);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
        plugin = null;
    }

    public boolean isFactionShieldActive(Location location) {
        if (!Board.getInstance().getFactionAt(new FLocation(location)).isNormal()) return false;
        else {
            List<Integer> protectedHours = Main.data.getIntegerList(Board.getInstance().getFactionAt(new FLocation(location)).getId() + ".hours");
            if (protectedHours.isEmpty()) return false;
            List<String> core = new ArrayList<>(Main.data.getStringList(Board.getInstance().getFactionAt(new FLocation(location)).getId() + ".core"));
            return core.contains(location.getChunk().getX() + ", " + location.getChunk().getZ()) && protectedHours.contains(Main.getPlugin().calendar.get(Calendar.HOUR_OF_DAY));
        }
    }

    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadConfigs() {
        try {
            dataFile = new File(this.getDataFolder(), "data.yml");
            if (!dataFile.exists()) dataFile.createNewFile();
            data = YamlConfiguration.loadConfiguration(dataFile);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

}
