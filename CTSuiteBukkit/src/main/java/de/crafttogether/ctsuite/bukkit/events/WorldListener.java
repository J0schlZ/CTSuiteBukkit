package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;

public class WorldListener implements Listener
{
    private CTSuite plugin;
    
    public WorldListener() {
        this.plugin = CTSuite.getInstance();
    }
    
    @EventHandler
    public void onWorldLoad(final WorldLoadEvent ev) {
        String worldName = ev.getWorld().getName();
        if (this.plugin.getWorldHandler().worlds.containsKey(worldName)) {
            this.plugin.getWorldHandler().worlds.put(worldName, Bukkit.getServer().getName());
        }
        NetworkMessage nm = new NetworkMessage("data.update.world.loaded");
        nm.put("world", worldName);
        nm.send("all");
    }
    
    @EventHandler
    public void onWorldUnload(final WorldUnloadEvent ev) {
        String worldName = ev.getWorld().getName();
        if (this.plugin.getWorldHandler().worlds.containsKey(worldName)) {
            this.plugin.getWorldHandler().worlds.remove(worldName);
        }
        NetworkMessage nm = new NetworkMessage("data.update.world.unloaded");
        nm.put("world", worldName);
        nm.send("all");
    }
}
