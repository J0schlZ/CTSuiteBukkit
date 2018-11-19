package de.crafttogether.ctsuite.bukkit.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class PluginMessage {
	private CTSuite main;
	
	private String messageName;
	private ArrayList<String> values;
	
	public PluginMessage(CTSuite main, String name) {
		this.main = main;
		this.messageName = name;
		values = new ArrayList<String>();
	}
	
	public void put(String value) {
		values.add(value);
	}
	
	public void send(Player p) {
		final String serverName = p.getServer().getServerName();
		
    	Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
    		@Override
    		public void run() {                
                try {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                        
                    out.writeUTF(messageName);
                    out.writeUTF(serverName);
                    for(int i = 0; i < values.size(); i++)
    	        		out.writeUTF("");
                    
                    b.close();
                    out.close();
                    p.sendPluginMessage(main, "ctsuite:bungee", b.toByteArray());
                    
                    main.getLogger().log(Level.INFO, "Send PluginMessage to Bungeecoord [" + messageName + "]");
                } catch (IOException e) {
                    e.printStackTrace();
                }
    		}
    	});
	}
}
