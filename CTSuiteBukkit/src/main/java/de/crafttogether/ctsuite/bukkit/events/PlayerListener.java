package de.crafttogether.ctsuite.bukkit.events;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;
import net.milkbowl.vault.chat.Chat;

public class PlayerListener implements Listener
{
    private CTSuite plugin;
    
    public PlayerListener() {
        this.plugin = CTSuite.getInstance();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent ev) {
    	Player p = ev.getPlayer();
    	
		if (plugin.getPlayerHandler().pendingTeleports.containsKey(p.getUniqueId())) {
			HashMap<String, Object> pendingTeleport = plugin.getPlayerHandler().pendingTeleports.get(p.getUniqueId());
			Location loc = (Location) pendingTeleport.get("location");
			
			plugin.getPlayerHandler().pendingTeleports.remove(p.getUniqueId());
			p.teleport(loc);
			
			System.out.println("PendingTeleport for " + p.getName() + " (" + (System.currentTimeMillis() / 1000L - (int) pendingTeleport.get("timestamp")) + ")");
		}
		
		this.plugin.getPlayerHandler().registerLogin(ev.getPlayer());
		
		Chat chat = this.plugin.getChat();
		String prefix = null;
		String suffix = null;
		
		if (chat != null) {
		    try {
		        prefix = this.plugin.getChat().getPlayerPrefix(p);
		        prefix = ((prefix != null) ? prefix : "");
		    suffix = this.plugin.getChat().getPlayerSuffix(p);
		    suffix = ((suffix != null) ? suffix : "");
		    }
		    catch (Exception ex) {}
		}
		
		NetworkMessage nMessage = new NetworkMessage("player.update.joined.server");
		nMessage.put("uuid", p.getUniqueId());
		nMessage.put("prefix", prefix);
		nMessage.put("suffix", suffix);
		nMessage.put("world", p.getWorld().getName());
		nMessage.send("all");
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent ev) {
        NetworkMessage nMessage = new NetworkMessage("player.update.world");
        nMessage.put("uuid", ev.getPlayer().getUniqueId());
        nMessage.put("world", ev.getPlayer().getWorld().getName());
        nMessage.send("all");
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent ev) {
        NetworkMessage nMessage = new NetworkMessage("player.update.flying");
        nMessage.put("uuid", ev.getPlayer().getUniqueId());
        nMessage.put("flying", ev.getPlayer().isFlying());
        nMessage.send("proxy");
    }
    
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent ev) {
        NetworkMessage nMessage = new NetworkMessage("player.update.gamemode");
        nMessage.put("uuid", ev.getPlayer().getUniqueId());
        nMessage.put("gamemode", ev.getPlayer().getGameMode());
        nMessage.send("proxy");
    }
}
