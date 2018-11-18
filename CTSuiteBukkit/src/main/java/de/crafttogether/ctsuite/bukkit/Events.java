package de.crafttogether.ctsuite.bukkit;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events
implements Listener {
    private CTSuite main;

    public Events(CTSuite main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent ev) {
        Bukkit.getServer().getConsoleSender().sendMessage("Huhu du joint");

        Bukkit.getScheduler().runTaskAsynchronously(CTSuite.getInstance(), new Runnable() {
        	public void run() {
        		Player p = ev.getPlayer();
                
        		try {
                    String sql = "SELECT * FROM `ct_players` WHERE `uuid` = \"" + p.getUniqueId().toString() + "\"";

                    ResultSet rs = Events.this.main.getHikari().getConnection().createStatement().executeQuery(sql);
                    if (rs.next()) {
                        p.sendMessage("Spieler gefunden: " + rs.getString("name"));
                    } else {
                        p.sendMessage("Hab dich nicht gefunden. Bist du neu? :)");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}