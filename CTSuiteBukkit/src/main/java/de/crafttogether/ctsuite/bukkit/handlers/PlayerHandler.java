package de.crafttogether.ctsuite.bukkit.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessageEvent;

public class PlayerHandler implements Listener
{
    private CTSuite plugin;
    public HashMap<UUID, String> uuids;
    public HashMap<UUID, String> onlinePlayers;
    
    public PlayerHandler() {
        this.plugin = CTSuite.getInstance();
        this.uuids = new HashMap<UUID, String>();
        this.onlinePlayers = new HashMap<UUID, String>();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }
    
    @EventHandler
    public void onNetworkMessage(final NetworkMessageEvent ev) {
        final String messageKey = ev.getMessageKey();
        switch (messageKey) {
            case "player.update.joined.server": {
                this.onPlayerJoinedServer(UUID.fromString((String)ev.getValue("uuid")), (String)ev.getValue("server"), (String)ev.getValue("world"));
                break;
            }
            case "player.update.joined.network": {
                this.onPlayerJoinedNetwork(UUID.fromString((String)ev.getValue("uuid")));
                break;
            }
            case "player.update.leaved.network": {
                this.onPlayerLeavedNetwork(UUID.fromString((String)ev.getValue("uuid")));
                break;
            }
            case "player.set.fly": {
                this.setFly(UUID.fromString((String)ev.getValue("uuid")), (Boolean)ev.getValue("fly"));
                break;
            }
        }
    }
    
    private void onPlayerJoinedServer(final UUID uuid, final String server, final String world) {
        this.onlinePlayers.put(uuid, server);
    }
    
    private void onPlayerJoinedNetwork(final UUID uuid) {
        this.onlinePlayers.put(uuid, null);
    }
    
    private void onPlayerLeavedNetwork(final UUID uuid) {
        if (this.onlinePlayers.containsKey(uuid)) {
            this.onlinePlayers.remove(uuid);
        }
    }
    
    public void setFly(final UUID uuid, final Boolean isAllowedFlight) {
        final Player p = Bukkit.getServer().getPlayer(uuid);
        if (p != null) {
            p.setAllowFlight((boolean)isAllowedFlight);
            if (!isAllowedFlight && p.isFlying()) {
                p.setFlying(false);
            }
        }
    }
    
    public void registerLogin(final Player p) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = this.plugin.getMySQLConnection();
            statement = connection.prepareStatement("SELECT * FROM " + this.plugin.getTablePrefix() + "players WHERE uuid = ?;");
            statement.setString(1, p.getUniqueId().toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Bukkit.getScheduler().runTaskAsynchronously((Plugin)CTSuite.getInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        PreparedStatement statement = null;
                        Connection connection = null;
                        try {
                            connection = PlayerHandler.this.plugin.getMySQLConnection();
                            statement = connection.prepareStatement("UPDATE " + PlayerHandler.this.plugin.getTablePrefix() + "players SET server = ?, world = ?, online = 1 WHERE uuid = ?;");
                            statement.setString(1, Bukkit.getServerName());
                            statement.setString(2, p.getWorld().getName());
                            statement.setString(3, p.getUniqueId().toString());
                            statement.execute();
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                            if (statement != null) {
                                try {
                                    statement.close();
                                }
                                catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            if (connection != null) {
                                try {
                                    connection.close();
                                }
                                catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        finally {
                            if (statement != null) {
                                try {
                                    statement.close();
                                }
                                catch (SQLException ex) {
                                	ex.printStackTrace();
                                }
                            }
                            if (connection != null) {
                                try {
                                    connection.close();
                                }
                                catch (SQLException ex) {
                                	ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
                if (resultSet.getInt("fly") == 1) {
                    p.setAllowFlight(true);
                    if (resultSet.getInt("flying") == 1) {
                        p.setFlying(true);
                    }
                }
                else {
                    p.setAllowFlight(false);
                }
                try {
                    final GameMode gm = GameMode.valueOf(resultSet.getString("gamemode"));
                    if (gm != null) {
                        p.setGameMode(gm);
                    }
                }
                catch (Exception ex) {}
            }
            else {
                p.sendMessage("[CTSuiteBukkit]: Hab dich leider nicht gefunden! Jaa dieser Fall kann auftreten. :(");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException ex) {
                	ex.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException ex) {
                	ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException ex) {
                	ex.printStackTrace();
                }
            }
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException ex) {
                	ex.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException ex) {
                	ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException ex) {
                	ex.printStackTrace();
                }
            }
        }
    }
    
    public void readPlayersFromDB() {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                ResultSet resultSet = null;
                PreparedStatement statement = null;
                Connection connection = null;
                try {
                    connection = PlayerHandler.this.plugin.getMySQLConnection();
                    statement = connection.prepareStatement("SELECT `uuid`,`name` FROM " + PlayerHandler.this.plugin.getTablePrefix() + "players");
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        PlayerHandler.this.uuids.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"));
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        }
                        catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        }
                        catch (SQLException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (SQLException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (SQLException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    
    public boolean hasPermission(final Player p, final String perm) {
        return p.isOp() || p.hasPermission(perm);
    }
    
    public boolean checkPermission(final Player p, final String perm) {
        if (this.hasPermission(p, perm)) {
            System.out.println("PermissionCheck for player " + p.getName() + " (" + perm + ") -> true");
            return true;
        }
        System.out.println("PermissionCheck for player " + p.getName() + " -> false");
        final NetworkMessage nm = new NetworkMessage("player.inform.permission.denied");
        nm.put("uuid", p.getUniqueId());
        nm.put("permission", perm);
        nm.send("proxy");
        return false;
    }
}
