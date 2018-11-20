package de.crafttogether.ctsuite.bukkit.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class PlayerHandler {
	private CTSuite main;
	
	public PlayerHandler(CTSuite main) {
        this.main = main;
	}
	
	public void registerLogin(Player p) {       
		try {
            String sql = "SELECT * FROM `" + main.getTablePrefix() + "players` WHERE `uuid` = '" + p.getUniqueId().toString() + "'";

            ResultSet rs = main.getHikari().getConnection().createStatement().executeQuery(sql);
            if (rs.next()) {           	
            	
            	// Update Database
                Bukkit.getScheduler().runTaskAsynchronously(CTSuite.getInstance(), new Runnable() {
                	public void run() {
                		try {
                			String sql = 
    		                  "UPDATE " + main.getTablePrefix() + "players SET " + 
    		                    "server = '" + Bukkit.getServerName() + "', " + 
    		                    "world = '" + p.getWorld().getName() + "', " +
    		                    "last_seen = now() " +
    		                  "WHERE uuid = '" + p.getUniqueId() + "'";
    		              
                			main.getHikari().getConnection().createStatement().execute(sql);
    		            } catch (SQLException e) {
    		                e.printStackTrace();
    		            }
                	}
                });
	          
	            p.sendMessage("[CTSuiteBukkit]: Gefunden, geupdated.");
            } else {
                p.sendMessage("[CTSuiteBukkit]: Hab dich leider nicht gefunden! Jaa dieser Fall kann auftreten. :(");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void setIsAllowedFlight(String uuid, Boolean isAllowedFlight) {
		OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
		System.out.println("try set fly " + isAllowedFlight);
		if (user.isOnline()) {
			Player p = Bukkit.getPlayer(uuid);
			
			if (isAllowedFlight) {
				p.setAllowFlight(false);
				p.setFlying(false);
			}
			else
				p.setAllowFlight(true);
		}
		else {
			// Set fly when player is offline
		}
	}
}
