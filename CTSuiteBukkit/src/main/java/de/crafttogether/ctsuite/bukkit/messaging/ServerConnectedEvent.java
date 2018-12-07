package de.crafttogether.ctsuite.bukkit.messaging;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public final class ServerConnectedEvent extends Event
{
    private static final HandlerList handlers;
    private String serverName;
    
    public ServerConnectedEvent(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getName() {
        return this.serverName;
    }
    
    public HandlerList getHandlers() {
        return ServerConnectedEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return ServerConnectedEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
