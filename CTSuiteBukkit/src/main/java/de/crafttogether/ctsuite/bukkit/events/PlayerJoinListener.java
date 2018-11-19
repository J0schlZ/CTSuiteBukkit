package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PluginMessage;

public class PlayerJoinListener implements Listener {
    private CTSuite main;

    public PlayerJoinListener(CTSuite main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent ev) {
    	Player p = ev.getPlayer();
    	String prefix = main.getChat().getPlayerPrefix(p);
    	String suffix = main.getChat().getPlayerSuffix(p);

    	final String fPrefix = (prefix != null && !prefix.equals("")) ? prefix : null;
    	final String fSuffix = (suffix != null && !suffix.equals("")) ? suffix : null;
    	
    	// Delay packet sending to avoid login issues
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleAsyncDelayedTask(main, new Runnable() {
            @Override
            public void run() {
            	// Send Message to CTSuiteBungee
        		PluginMessage pm = new PluginMessage(main, "bungee.player.updatePrefixSuffix");
        		pm.put(p.getUniqueId().toString());
        		pm.put(fPrefix);
        		pm.put(fSuffix);
        		pm.send(p);
            }
        }, 20L);
    	
    	main.getPlayerHandler().registerLogin(ev.getPlayer());
    }
}