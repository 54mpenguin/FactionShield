package me.fiftyfour.factionshield.listeners;

import me.fiftyfour.factionshield.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;


public class ExplosionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> Main.getPlugin().isFactionShieldActive(block.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent e) {
        e.blockList().removeIf(block -> Main.getPlugin().isFactionShieldActive(block.getLocation()));
    }
}
