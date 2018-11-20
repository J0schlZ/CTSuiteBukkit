package de.crafttogether.ctsuite.bukkit.util;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class PMessageListener implements PluginMessageListener {
    private CTSuite main;

    public PMessageListener(CTSuite main) {
        this.main = main;
    }

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		ArrayList<String> values = new ArrayList<String>();                    

        String value;
		String messageName = null;
        
        try {
            messageName = in.readUTF();
            
            try {
            	while ((value = in.readUTF()) != null)
            		values.add(value);
            } catch (Exception e) { }
		}
        catch (Exception e) {
        	e.printStackTrace();
        }

        main.getLogger().log(Level.INFO, "[PMessage][Bungee->" + Bukkit.getServerName() + "]: " + messageName);
        
        switch(messageName) {        	
        		
        	case "bukkit.player.set.isAllowedFlight":
        		/*
        		 * 0 => (str)	uuid
        		 * 1 => (bool)	isAllowedFlight
        		 */
        		System.out.println(values.get(0) + "-" + values.get(1));
        		main.getPlayerHandler().setIsAllowedFlight(values.get(0), (values.get(1).equals("true") ? true : false));
        		break;
        }
    }
}