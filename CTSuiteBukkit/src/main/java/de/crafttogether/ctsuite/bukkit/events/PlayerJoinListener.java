package de.crafttogether.ctsuite.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;
import net.milkbowl.vault.chat.Chat;

public class PlayerJoinListener implements Listener
{
    private CTSuite plugin;
    
    public PlayerJoinListener() {
        this.plugin = CTSuite.getInstance();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent ev) {
        final Player p = ev.getPlayer();
        final Chat chat = this.plugin.getChat();
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
        this.plugin.getPlayerHandler().registerLogin(ev.getPlayer());
        final NetworkMessage nMessage = new NetworkMessage("player.update.joined.server");
        nMessage.put("uuid", p.getUniqueId());
        nMessage.put("prefix", prefix);
        nMessage.put("suffix", suffix);
        nMessage.put("world", p.getWorld().getName());
        nMessage.send("all");
    }
}
