package de.crafttogether.ctsuite.bukkit.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

public class PlayerHandler {
    private CTSuite main;
    
    public HashMap<String, String> bungeeOnlinePlayers;
    
    public PlayerHandler(CTSuite main) {
        this.main = main;
        this.bungeeOnlinePlayers = new HashMap<String, String>();
    }

    public void registerLogin(Player p) {
    	ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;
        
        try {
        	connection = main.getConnection();
			statement = connection.prepareStatement("SELECT * FROM " + main.getTablePrefix() + "players WHERE uuid = ?;");
			statement.setString(1, p.getUniqueId().toString());
			resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Update Database
                Bukkit.getScheduler().runTaskAsynchronously(CTSuite.getInstance(), new Runnable() {
                    public void run() {
                        PreparedStatement statement = null;
                        Connection connection = null;
                        
		                try {
		                	connection = main.getConnection();
							statement = connection.prepareStatement("UPDATE " + main.getTablePrefix() + "players SET server = ?, world = ?, last_seen = now() WHERE uuid = ?;");
							statement.setString(1, Bukkit.getServerName());
							statement.setString(2, p.getWorld().getName());
							statement.setString(3, p.getUniqueId().toString());
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

                // Set Fly-Mode
                if (resultSet.getInt("allowed_flight") == 1) {
                    p.setAllowFlight(true);
                    if (resultSet.getInt("flying") == 1)
                        p.setFlying(true);
                } else
                    p.setAllowFlight(false);

            } else {
                p.sendMessage("[CTSuiteBukkit]: Hab dich leider nicht gefunden! Jaa dieser Fall kann auftreten. :(");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try { resultSet.close(); }
                catch (SQLException e) { e.printStackTrace(); }
            }
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

    public boolean hasPermission(Player p, String perm) {
        if (p.isOp() || p.hasPermission(perm))
            return true;

        return false;
    }

    public boolean checkPermission(Player p, String perm) {
        if (hasPermission(p, perm)) {
            System.out.println("PermissionCheck for player " + p.getName() + " -> true");
            return true;
        }

        System.out.println("PermissionCheck for player " + p.getName() + " -> false");
        PMessage pm = new PMessage(main, "bungee.player.inform.permissionDenied");
        pm.put(p.getUniqueId().toString());
        pm.put(perm);
        pm.send(p);

        return false;
    }

    public boolean isOnline(String uuid) {
        Collection < ? extends Player > players = Bukkit.getServer().getOnlinePlayers();
        for (Player p: players) {
            if (p.getUniqueId().toString().equals(uuid))
                return true;
        }

        return false;
    }

    public Player getOnlinePlayer(String uuid) {
        Collection < ? extends Player > players = Bukkit.getServer().getOnlinePlayers();
        for (Player p: players) {
            if (p.getUniqueId().toString().equals(uuid))
                return p;
        }

        return null;
    }

    public void setIsAllowedFlight(String uuid, Boolean isAllowedFlight) {
        Player p = getOnlinePlayer(uuid);

        if (p != null)
            p.setAllowFlight(isAllowedFlight);
    }
}