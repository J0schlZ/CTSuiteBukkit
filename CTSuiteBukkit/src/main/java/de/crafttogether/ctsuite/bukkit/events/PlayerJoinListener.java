package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PluginMessage;

public class PlayerJoinListener implements Listener {
    private CTSuite main;

    public PlayerJoinListener(CTSuite main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
    	Player p = ev.getPlayer();
    	String prefix = main.getChat().getPlayerPrefix(p);
    	String suffix = main.getChat().getPlayerSuffix(p);

    	prefix = (prefix != null && !prefix.equals("")) ? prefix : null;
    	suffix = (suffix != null && !suffix.equals("")) ? suffix : null;
    	
    	// Send Message to CTSuiteBungee
		PluginMessage pm = new PluginMessage(main, "bungee.player.updatePrefixSuffix");
		pm.put(p.getUniqueId().toString());
		pm.put(prefix);
		pm.put(suffix);
		pm.send(p);
    	
    	main.getPlayerHandler().registerLogin(ev.getPlayer());
    }
}