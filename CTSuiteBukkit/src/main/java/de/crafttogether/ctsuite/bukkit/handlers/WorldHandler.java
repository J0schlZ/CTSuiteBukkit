package de.crafttogether.ctsuite.bukkit.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessageEvent;
import de.crafttogether.ctsuite.bukkit.messaging.ServerConnectedEvent;
import de.crafttogether.ctsuite.bukkit.messaging.ServerDisconnectEvent;

public class WorldHandler implements Listener
{
    private CTSuite plugin;
    public ArrayList<String> server;
    public HashMap<String, String> worlds;
    
    public WorldHandler() {
        this.plugin = CTSuite.getInstance();
        this.server = new ArrayList<String>();
        this.worlds = new HashMap<String, String>();
        for (final World world : Bukkit.getWorlds()) {
            this.worlds.put(world.getName(), Bukkit.getServerName());
        }
        final MultiverseCore multiverse = CTSuite.getInstance().getMultiverseCore().getCore();
        if (multiverse != null) {
            final Collection<MultiverseWorld> MVWorlds = (Collection<MultiverseWorld>)multiverse.getMVWorldManager().getMVWorlds();
            for (final MultiverseWorld MVWorld : MVWorlds) {
                if (!this.worlds.containsKey(MVWorld.getName())) {
                    this.worlds.put(MVWorld.getName(), Bukkit.getServerName());
                }
            }
        }
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }
    
    @EventHandler
    public void onServerConnected(final ServerConnectedEvent event) {
        if (!this.server.contains(event.getName())) {
            this.server.add(event.getName());
        }
        final ArrayList<String> worlds = new ArrayList<String>();
        for (final Map.Entry<String, String> entry : this.worlds.entrySet()) {
            if (entry.getValue().equals(Bukkit.getServerName())) {
                worlds.add(entry.getKey() + ":" + entry.getValue());
            }
        }
        new BukkitRunnable() {
            public void run() {
                final NetworkMessage nm = new NetworkMessage("data.update.world.list");
                nm.put("worlds", worlds);
                nm.send(event.getName());
            }
        }.runTaskLaterAsynchronously((Plugin)this.plugin, 20L);
    }
    
    @EventHandler
    public void onServerDisconnect(final ServerDisconnectEvent ev) {
        if (this.server.contains(ev.getName())) {
            this.server.remove(ev.getName());
        }
    }

	@EventHandler
    @SuppressWarnings("unchecked")
    public void onNetworkMessage(final NetworkMessageEvent ev) {        
        switch (ev.getMessageKey())
        {
            case "data.update.world.list": {
                ArrayList<String> worlds = (ArrayList<String>) ev.getValues().get("worlds");
                
                for (String world : worlds) {
                    String[] worldData = world.split(":");
                    this.worlds.put(worldData[0], worldData[1]);
                }
                break;
            }
            case "data.update.world.loaded": {
                this.worlds.put((String) ev.getValues().get("world"), ev.getSender());
                break;
            }
            case "data.update.world.unloaded": {
                this.worlds.remove((String) ev.getValues().get("world"), ev.getSender());
                break;
            }
        }
    }
}
