package de.crafttogether.ctsuite.bukkit.messaging.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessageEvent;
import de.crafttogether.ctsuite.bukkit.messaging.ServerConnectedEvent;
import de.crafttogether.ctsuite.bukkit.messaging.ServerDisconnectEvent;
import fr.rhaz.minecraft.sockets.Sockets4Bukkit;
import fr.rhaz.sockets.Connection;
import fr.rhaz.sockets.MultiSocket;

public class Sockets4MC
{
    private static Sockets4MC instance;
    private CTSuite plugin;
    
    public Sockets4MC() {
        Sockets4MC.instance = this;
        
        Sockets4Bukkit.onSocketEnable((JavaPlugin)(this.plugin = CTSuite.getInstance()), "default", socket -> {
            this.plugin.getLogger().info("Socket #default is available");
            socket.onReady(connection -> {
                this.plugin.getLogger().info("Connection to " + connection.getTargetName() + " is available");
                ServerConnectedEvent event = new ServerConnectedEvent(connection.getTargetName());
                Bukkit.getServer().getPluginManager().callEvent(event);
                return;
            });
            socket.onDisconnect(connection -> {
                this.plugin.getLogger().info("Lost connection to " + connection.getTargetName());
                ServerDisconnectEvent event = new ServerDisconnectEvent(connection.getTargetName());
                Bukkit.getServer().getPluginManager().callEvent(event);
                return;
            });
            socket.onMessage("ctsuite", (connection, msg) -> {
                HashMap<String, Object> values = new HashMap<String, Object>();
                
                Iterator<Entry<String, Object>> iterator = msg.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if (!entry.getKey().equalsIgnoreCase("messageKey") && !entry.getKey().equalsIgnoreCase("channel"))
                        values.put(entry.getKey(), entry.getValue());
                }
                
                String sender = connection.getTargetName();
                String messageKey = msg.get("messageKey").toString();
                NetworkMessageEvent event = new NetworkMessageEvent(sender, messageKey, values);
                Bukkit.getServer().getPluginManager().callEvent(event);
                CTSuite.getInstance().getLogger().info("[NMessage] (" + sender + " -> " + Bukkit.getServerName() + "): " + messageKey);
            });
        });
    }
    
    @SuppressWarnings("unchecked")
	public void send(final String messageKey, final String serverName, final HashMap<String, Object> values) {
        MultiSocket socket = null;
        socket = fr.rhaz.minecraft.sockets.Sockets4MC.getSocket("default");
        
        if (socket == null) {
            System.out.println("Socket #default is not available");
            return;
        }
        
        Map<String, Connection> peers = (Map<String, Connection>)socket.getPeers();
        if (serverName.equalsIgnoreCase("all")) {
            peers = (Map<String, Connection>)socket.getPeers();
            for (final Map.Entry<String, Connection> entry : peers.entrySet()) {
                final Connection conn = entry.getValue();
                this.send(messageKey, conn.getTargetName(), values);
            }
            return;
        }
        
        if (serverName.equalsIgnoreCase("servers")) {
            peers = (Map<String, Connection>)socket.getPeers();
            for (final Map.Entry<String, Connection> entry : peers.entrySet()) {
                final Connection conn = entry.getValue();
                if (conn.getTargetName().equalsIgnoreCase("proxy")) {
                    continue;
                }
                this.send(messageKey, conn.getTargetName(), values);
            }
            return;
        }
        
        if (serverName.contains(",")) {
            final String[] serverNames = serverName.split(",");
            for (String server : serverNames)
                this.send(messageKey, server, values);
            return;
        }
        
        final Connection connection = socket.getConnection(serverName);
        if (connection == null) {
            System.out.println("Connection to " + serverName + " is not available");
            return;
        }
        
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put("messageKey", (Object) messageKey);
        
        for (final Map.Entry<String, Object> entry : values.entrySet()) {
            jsonObj.put(entry.getKey(), entry.getValue());
        }
        
        connection.msg("ctsuite", jsonObj);
        CTSuite.getInstance().getLogger().info("[NMessage] (" + Bukkit.getServerName() + " -> " + serverName + "): " + messageKey);
    }
    
    public static Sockets4MC getInstance() {
        return Sockets4MC.instance;
    }
}
