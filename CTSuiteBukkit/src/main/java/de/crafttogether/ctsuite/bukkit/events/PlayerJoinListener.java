package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;
import net.milkbowl.vault.chat.Chat;

public class PlayerJoinListener implements Listener {
    private CTSuite main;

    public PlayerJoinListener(CTSuite main) {
        this.main = main;
    }
    
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent ev) {
        Player p = ev.getPlayer();
        
        Chat chat = main.getChat();
        String prefix = null;
        String suffix = null;
        
        if (chat != null) {
	        try {
	        	prefix = main.getChat().getPlayerPrefix(p);
	        	suffix = main.getChat().getPlayerSuffix(p);
	        } catch (Exception ex) { }
        }

        final String fPrefix = (prefix != null) ? prefix : "";
        final String fSuffix = (suffix != null) ? suffix : "";

        main.getPlayerHandler().registerLogin(ev.getPlayer());

        new BukkitRunnable() {
            public void run() {
                PMessage pm = new PMessage(main, "bungee.player.update.joined");
                pm.put(p.getUniqueId().toString());
                pm.put(p.getServer().getName());
                pm.put(p.getWorld().getName());
                pm.put(fPrefix);
                pm.put(fSuffix);
                pm.send(p);
            }
        }.runTaskLater(main, 20L);
    }
}