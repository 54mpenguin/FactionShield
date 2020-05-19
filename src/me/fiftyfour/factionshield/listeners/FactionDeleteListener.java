package me.fiftyfour.factionshield.listeners;

import com.massivecraft.factions.event.FactionDisbandEvent;
import me.fiftyfour.factionshield.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionDeleteListener implements Listener {

    @EventHandler
    public void onFactionDelete(FactionDisbandEvent event){
        if (!Main.data.getIntegerList(event.getFaction().getId() + ".hours").isEmpty()){
            Main.data.set(event.getFaction().getId(), null);
        }
    }
}
