package de.crafttogether.ctsuite.bukkit.util;

import java.util.ArrayList;
import java.util.HashMap;
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
        ArrayList < String > values = new ArrayList < String > ();

        String val;
        String messageName = null;

        try {
            messageName = in .readUTF();

            try {
                while ((val = in .readUTF()) != null)
                    values.add(val);
            } catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }

        main.getLogger().log(Level.INFO, "[PMessage][Bungee->" + Bukkit.getServerName() + "]: " + messageName);

        switch (messageName) {
        
        	case "bukkit.data.update.onlinePlayers":
        		/*
        		 * 0 => (str)	uuid:name
        		 * 1 => (str)	uuid:name,
        		 * 2 => ...	
        		 */
        		HashMap<String, String> newList = new HashMap<String, String>();
        		for (String value : values) {
        			String[] splitted = value.split(":");
        			newList.put(splitted[0], splitted[1]);
        		}
        		main.getPlayerHandler().bungeeOnlinePlayers = newList;
        		break;

            case "bukkit.player.set.isAllowedFlight":
                /*
                 * 0 => (str)	uuid
                 * 1 => (bool)	isAllowedFlight
                 */
                main.getPlayerHandler().setIsAllowedFlight(values.get(0), (values.get(1).equals("true") ? true : false));
                break;

            case "bukkit.player.set.gameMode":
                /*
                 * 0 => (str)	uuid
                 * 1 => (bool)	isAllowedFlight
                 */
                main.getPlayerHandler().setGameMode(values.get(0), values.get(1));
                break;
        }
    }
}