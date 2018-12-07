package de.crafttogether.ctsuite.bukkit.messaging;

import java.util.HashMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public final class NetworkMessageEvent extends Event
{
    private static final HandlerList handlers;
    private String sender;
    private String messageKey;
    private HashMap<String, Object> values;
    
    public NetworkMessageEvent(final String sender, final String messageKey, final HashMap<String, Object> values) {
        this.sender = sender;
        this.messageKey = messageKey;
        this.values = values;
    }
    
    public String getMessageKey() {
        return this.messageKey;
    }
    
    public String getSender() {
        return this.sender;
    }
    
    public HashMap<String, Object> getValues() {
        return this.values;
    }
    
    public Object getValue(final Object key) {
        return this.values.get(key);
    }
    
    public HandlerList getHandlers() {
        return NetworkMessageEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return NetworkMessageEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
