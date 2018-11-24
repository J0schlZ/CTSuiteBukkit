package de.crafttogether.ctsuite.bukkit.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class PMessage {
    private CTSuite main;

    private String messageName;
    private ArrayList < String > values;

    public PMessage(CTSuite main, String name) {
        this.main = main;
        this.messageName = name;
        this.values = new ArrayList <String> ();
    }

    public void put(String value) {
        this.values.add(value);
    }

    public void send(Player p) {
        Iterator <? extends Player> iterator;
        
        if ((p == null) || (!p.isOnline())) {
            Collection <? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            
            for (iterator = onlinePlayers.iterator(); iterator.hasNext();) {
                p = (Player) iterator.next();
                if (p.isOnline()) {
                    break;
                }
            }
        }
        
        if (p == null) {
            this.main.getLogger().warning("[CTSuite] Unable to send PMessage (" + this.messageName + ") - No player found");
        } else {
            final Player finalPlayer = p;
            final String serverName = finalPlayer.getServer().getServerName();
            final ArrayList <String> values = this.values;

	        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
	            @Override
	            public void run() {
	                ByteArrayOutputStream b = new ByteArrayOutputStream();
	                DataOutputStream out = new DataOutputStream(b);
	
	                try {
	                    out.writeUTF(messageName);
	                    out.writeUTF(serverName);
	                    for (int i = 0; i < values.size(); i++)
	                        out.writeUTF(values.get(i));
	
	                    b.close();
	                    out.close();
	                    finalPlayer.sendPluginMessage(main, "ctsuite:bungee", b.toByteArray());
	                    main.getLogger().log(Level.INFO, "[PMessage][" + serverName + "->Bungee]: " + messageName);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                } finally {
	                    try {
	                        b.close();
	                        out.close();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        });
        }
    }
}