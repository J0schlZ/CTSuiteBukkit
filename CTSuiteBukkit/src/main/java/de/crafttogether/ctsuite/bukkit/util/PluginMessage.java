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
	
	private String name;
	private ArrayList<String> values;
	
	public PluginMessage(CTSuite main, String name) {
		this.main = main;
		this.name = name;
		values = new ArrayList<String>();
	}
	
	public void put(String value) {
		values.add(value);
	}
	
	public void send(Player p) {
    	Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
    		@Override
    		public void run() {
    			ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                
                try {
                    out.writeUTF(name);
                    
                    for(int i = 0; i < values.size(); i++) {
    	        		out.writeUTF("" + values.get(i).toString());
    	    		}
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                p.sendPluginMessage(main, "CTSuite", b.toByteArray());
            }
    	});
	}
}
