package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class PlayerJoinListener implements Listener {
    private CTSuite main;

    public PlayerJoinListener(CTSuite main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
    	main.getPlayerHandler().registerLogin(ev.getPlayer());
    }
}