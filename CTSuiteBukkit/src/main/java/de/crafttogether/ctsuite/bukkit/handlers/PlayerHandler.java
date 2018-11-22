package de.crafttogether.ctsuite.bukkit.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

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

                // Set Fly-Mode
                if (rs.getInt("allowed_flight") == 1) {
                    p.setAllowFlight(true);
                    if (rs.getInt("flying") == 1)
                        p.setFlying(true);
                } else
                    p.setAllowFlight(false);

            } else {
                p.sendMessage("[CTSuiteBukkit]: Hab dich leider nicht gefunden! Jaa dieser Fall kann auftreten. :(");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        Collection << ? extends Player > players = Bukkit.getServer().getOnlinePlayers();
        for (Player p: players) {
            if (p.getUniqueId().toString().equals(uuid))
                return true;
        }

        return false;
    }

    public Player getOnlinePlayer(String uuid) {
        Collection << ? extends Player > players = Bukkit.getServer().getOnlinePlayers();
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