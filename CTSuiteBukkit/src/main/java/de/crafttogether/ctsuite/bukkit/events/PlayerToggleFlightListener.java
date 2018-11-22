package de.crafttogether.ctsuite.bukkit.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
                PreparedStatement statement = null;
                Connection connection = null;
                
                try {
                	connection = main.getConnection();
					statement = connection.prepareStatement("UPDATE " + main.getTablePrefix() + "players SET flying = ?, last_seen = now() WHERE uuid = ?;");
					statement.setInt(1, (p.isFlying() ? 1 : 0));
					statement.setString(2, p.getUniqueId().toString());
					statement.execute();
    			} catch (SQLException e) {
					e.printStackTrace();
				} finally {
		            if (statement != null) {
		                try { statement.close(); }
		                catch (SQLException e) { e.printStackTrace(); }
		            }
		            if (connection != null) {
		                try { connection.close(); }
		                catch (SQLException e) { e.printStackTrace(); }
		            }
		        }
            }
        });

    }
}