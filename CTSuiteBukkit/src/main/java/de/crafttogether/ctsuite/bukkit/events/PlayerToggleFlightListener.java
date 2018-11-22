package de.crafttogether.ctsuite.bukkit.events;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class PlayerToggleFlightListener implements Listener {
    private CTSuite main;

    public PlayerToggleFlightListener(CTSuite main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent ev) {
        Player p = ev.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(CTSuite.getInstance(), new Runnable() {
            public void run() {
                try {
                    String sql =
                        "UPDATE " + main.getTablePrefix() + "players SET " +
                        "flying = " + (p.isFlying() ? 1 : 0) + ", " +
                        "last_seen = now() " +
                        "WHERE uuid = '" + p.getUniqueId() + "'";

                    main.getHikari().getConnection().createStatement().execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}