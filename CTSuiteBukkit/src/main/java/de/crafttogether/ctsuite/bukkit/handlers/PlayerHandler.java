package de.crafttogether.ctsuite.bukkit.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessageEvent;
import de.crafttogether.ctsuite.bukkit.util.CTLocation;

public class PlayerHandler implements Listener
{
    private CTSuite plugin;
    public HashMap<UUID, String> uuids; // uuid, name
    public HashMap<UUID, HashMap<String, Object>> onlinePlayers;
    public HashMap<UUID, HashMap<String, Object>> pendingTeleports;
    
    public PlayerHandler() {
        this.plugin = CTSuite.getInstance();
        this.uuids = new HashMap<UUID, String>();
        this.onlinePlayers = new HashMap<UUID, HashMap<String, Object>>();
        this.pendingTeleports = new HashMap<UUID, HashMap<String, Object>>();
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

	@EventHandler
    @SuppressWarnings("unchecked")
    public void onNetworkMessage(NetworkMessageEvent ev) {
        String messageKey = ev.getMessageKey();
        switch (messageKey) {
        	case "update.data.players.list":
        		this.onPlayerList(
        			(ArrayList<String>) ev.getValue("players")
        		);
        	break;
     
            case "player.request.location":
                this.onLocationRequest(
                	UUID.fromString((String) ev.getValue("uuid")),
                	(String) ev.getValue("requestId"),
                	ev.getSender()
                );
                break;
     
            case "player.update.joined.server":
                this.onPlayerJoinedServer(
                	UUID.fromString((String) ev.getValue("uuid")),
                	(String) ev.getValue("server"),
                	(String) ev.getValue("world")
                );
                break;
                
            case "player.update.joined.network": 
                this.onPlayerJoinedNetwork(
                    UUID.fromString((String) ev.getValue("uuid")),
                    (String) ev.getValue("name")
                );
                break;
            
            case "player.update.leaved.network": 
                this.onPlayerLeavedNetwork(
                	UUID.fromString((String) ev.getValue("uuid"))
                );
                break;
            
            case "player.update.world": 
                this.onPlayerWorld(
                	UUID.fromString((String) ev.getValue("uuid")),
                	(String) ev.getValue("world")
                );
                break;
            
            case "player.set.fly": 
                this.setFly(
                	UUID.fromString((String) ev.getValue("uuid")),
                	(Boolean) ev.getValue("fly")
                );
                break;
                
            case "player.set.gamemode": 
                this.setGamemode(
                	UUID.fromString((String) ev.getValue("uuid")),
                	(String) ev.getValue("gamemode")
                );
                break;
                
        case "player.teleport.location": 
            this.onPlayerTeleportLocation(
				UUID.fromString((String) ev.getValue("uuid")),
				CTLocation.fromString((String) ev.getValue("location"))
            );
            break;
		        
		case "player.teleport.player": 
		    this.onPlayerTeleportPlayer(
				UUID.fromString((String) ev.getValue("playerUUID")),
				UUID.fromString((String) ev.getValue("targetUUID"))
		    );
		    break;
        }
    }

	private void onLocationRequest(UUID uuid, String requestId, String sender) {
		Player p = Bukkit.getPlayer(uuid);
		
		if (p == null)
			return;
		
		CTLocation loc = new CTLocation(p.getLocation(), Bukkit.getServerName());
		NetworkMessage nm = new NetworkMessage("player.response.location");
		
		nm.put("location", loc.toString());
		nm.put("requestId", requestId);
    	nm.send(sender);
	}

	private void onPlayerList(ArrayList<String> playerList) {
    	// 'uuid:name:server:world'
		this.onlinePlayers = new HashMap<UUID, HashMap<String, Object>>();
    	for (String _playerData : playerList) {
    		String[] playerData = _playerData.split(":");
    		UUID uuid = UUID.fromString(playerData[0]);
    		
    		HashMap<String, Object> playerInfo = new HashMap<String, Object>();
    		playerInfo.put("uuid", uuid);
    		playerInfo.put("name", playerData[1]);
    		playerInfo.put("server", playerData[2]);
    		playerInfo.put("worlds", playerData[3]);
 
    		this.uuids.put(uuid, playerData[1]);
    		this.onlinePlayers.put(uuid, playerInfo);
    	}
	}

	private void onPlayerWorld(UUID uuid, String world) {
		if (!this.onlinePlayers.containsKey(uuid))
    		return;
		
		this.onlinePlayers.get(uuid).put("world", world);
	}

	private void onPlayerJoinedServer(UUID uuid, String server, String world) {
    	if (!this.onlinePlayers.containsKey(uuid))
    		return;
    	
    	this.onlinePlayers.get(uuid).put("server", server);
		this.onlinePlayers.get(uuid).put("world", world);
    }
    
    private void onPlayerJoinedNetwork(UUID uuid, String name) {
    	HashMap<String, Object> playerInfo = new HashMap<String, Object>();
    	playerInfo.put("uuid", uuid);
    	playerInfo.put("name", name);
        this.onlinePlayers.put(uuid, playerInfo);
    }
    
    private void onPlayerLeavedNetwork(UUID uuid) {
        if (this.onlinePlayers.containsKey(uuid))
            this.onlinePlayers.remove(uuid);
    }
    
    public void setFly(UUID uuid, Boolean isAllowedFlight) {
        Player p = Bukkit.getServer().getPlayer(uuid);
        
        if (p != null) {
            p.setAllowFlight((boolean)isAllowedFlight);
            if (!isAllowedFlight && p.isFlying())
                p.setFlying(false);
        }
    }
    
    private void setGamemode(UUID uuid, String gamemode) {
    	Player p = Bukkit.getServer().getPlayer(uuid);
        GameMode gm = null;
        
        switch(gamemode.toUpperCase()) {
        	case "SURVIVAL": gm = GameMode.SURVIVAL; break;
        	case "CREATIVE": gm = GameMode.CREATIVE; break;
        	case "ADVENTURE": gm = GameMode.ADVENTURE; break;
        	case "SPECTATOR": gm = GameMode.SPECTATOR; break;
        }
      
        if (p != null && gm != null)
            p.setGameMode(gm);
	}
    
	private void onPlayerTeleportLocation(UUID uuid, CTLocation ctLoc) {
		Player p = Bukkit.getServer().getPlayer(uuid);
		Location loc = ctLoc.getLocation();
		
		if (loc == null)
			return;
		
		if (p != null && p.isOnline())
			p.teleport(loc);
		else
			storeTeleport(uuid, loc);
	}
	
	private void onPlayerTeleportPlayer(UUID playerUUID, UUID targetUUID) {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		Player target = Bukkit.getServer().getPlayer(targetUUID);
		
		if (target == null || !target.isOnline())
			return;
		
		if (player != null && player.isOnline())
			player.teleport(target);
		else
			storeTeleport(playerUUID, targetUUID);
	}
	
	public void storeTeleport(UUID uuid, Location loc) {
		if (!this.uuids.containsKey(uuid))
			return;
		
		HashMap<String, Object> pendingTeleport = new HashMap<String, Object>();
		pendingTeleport.put("uuid", uuid);
		pendingTeleport.put("location", loc);
		pendingTeleport.put("timestamp", (int) (System.currentTimeMillis() / 1000L));
		
		this.pendingTeleports.put(uuid, pendingTeleport);
	}
	
	public void storeTeleport(UUID playerUUID, UUID targetUUID) {
		if (!this.uuids.containsKey(playerUUID))
			return;
		
		HashMap<String, Object> pendingTeleport = new HashMap<String, Object>();
		pendingTeleport.put("playerUUID", playerUUID);
		pendingTeleport.put("targetUUID", targetUUID);
		pendingTeleport.put("timestamp", (int) (System.currentTimeMillis() / 1000L));
		
		this.pendingTeleports.put(playerUUID, pendingTeleport);
	}
    
    public void registerLogin(Player p) {
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
                        }

                        if (statement != null) {
                            try { statement.close(); }
                            catch (SQLException ex) { ex.printStackTrace(); }
                        }
                        if (connection != null) {
                            try { connection.close(); }
                            catch (SQLException ex) { ex.printStackTrace(); }
                        }
                    }
                });
                
                String strLoginLocation = resultSet.getString("login_location");
                if (strLoginLocation != null) {
                	CTLocation loginLocation = CTLocation.fromString(strLoginLocation);
                	
                	if (!loginLocation.getServer().equalsIgnoreCase(Bukkit.getServer().getName()))
                		return;

                	p.teleport(loginLocation.getLocation());
                }
                else {
                	// spawn handling.. firstspawn etc
                }
                
                // Set Fly-Mode
                if (resultSet.getInt("fly") == 1) {
                    p.setAllowFlight(true);
                    if (resultSet.getInt("flying") == 1)
                        p.setFlying(true);
                }
                else
                    p.setAllowFlight(false);
       
                // Set GameMode
                try {
                    GameMode gm = GameMode.valueOf(resultSet.getString("gamemode"));
                    if (gm != null)
                        p.setGameMode(gm);
                }
                catch (Exception ex) { }
            }
            else
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[CTSuiteBukkit]: &cError: player not found in database"));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (resultSet != null) {
            try { resultSet.close(); }
            catch (SQLException ex) { ex.printStackTrace(); }
        }
        if (statement != null) {
            try { statement.close(); }
            catch (SQLException ex) { ex.printStackTrace(); }
        }
        if (connection != null) {
            try { connection.close(); }
            catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
    
    public void readPlayersFromDB() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                ResultSet resultSet = null;
                PreparedStatement statement = null;
                Connection connection = null;
                try {
                    connection = plugin.getMySQLConnection();
                    statement = connection.prepareStatement("SELECT `uuid`,`name` FROM " + plugin.getTablePrefix() + "players");
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        PlayerHandler.this.uuids.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"));
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                if (resultSet != null) {
                	try { resultSet.close(); }
                    catch (SQLException ex) { ex.printStackTrace(); }
                }
                if (statement != null) {
                	try { statement.close(); }
                    catch (SQLException ex) { ex.printStackTrace(); }
                }
                if (connection != null) {
                    try { connection.close(); }
                    catch (SQLException ex) { ex.printStackTrace(); }
                }
            }
        });
    }
    
    public boolean hasPermission(Player p, String perm) {
        return p.isOp() || p.hasPermission(perm);
    }
    
    public boolean hasPermission(CommandSender sender, String perm) {
        return sender.isOp() || sender.hasPermission(perm);
    }
    
    public boolean checkPermission(CommandSender sender, String perm) {
    	if (sender instanceof Player)
    		return checkPermission((Player) sender, perm);
    	else
    		return hasPermission(sender, perm);
    }
    
    public boolean checkPermission(Player p, String perm) {
        if (this.hasPermission(p, perm)) {
            System.out.println("PermissionCheck for player " + p.getName() + " (" + perm + ") -> true");
            return true;
        }
        
        System.out.println("PermissionCheck for player " + p.getName() + " -> false");
        NetworkMessage nm = new NetworkMessage("player.inform.permission.denied");
        nm.put("uuid", p.getUniqueId());
        nm.put("permission", perm);
        nm.send("proxy");
        return false;
    }
}
