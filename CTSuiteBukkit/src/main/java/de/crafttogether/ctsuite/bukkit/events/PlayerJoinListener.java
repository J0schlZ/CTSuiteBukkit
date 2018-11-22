package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

public class PlayerJoinListener implements Listener {
    private CTSuite main;

    public PlayerJoinListener(CTSuite main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent ev) {
        Player p = ev.getPlayer();

        String prefix = main.getChat().getPlayerPrefix(p);
        String suffix = main.getChat().getPlayerSuffix(p);

        final String fPrefix = (prefix != null && !prefix.equals("")) ? prefix : null;
        final String fSuffix = (suffix != null && !suffix.equals("")) ? suffix : null;

        main.getPlayerHandler().registerLogin(ev.getPlayer());

        new BukkitRunnable() {
            public void run() {
                PMessage pm = new PMessage(main, "bungee.player.updatePrefixSuffix");
                pm.put(p.getUniqueId().toString());
                pm.put("prefix - " + fPrefix);
                pm.put("suffix - " + fSuffix);
                pm.send(p);
            }
        }.runTaskLater(main, 20L);
    }
}