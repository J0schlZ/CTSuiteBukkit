package de.crafttogether.ctsuite.bukkit.commands;


import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

public class FlyCommand implements CommandExecutor  {
    private CTSuite main;

    public FlyCommand(CTSuite main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
    	Player p = null;
    	
    	if (sender instanceof Player)
    		p = Bukkit.getPlayer(((Player) sender).getUniqueId());
    	
    	// Self
    	if (args.length < 1 || args[0] == "true" || args[0] == "false") {
    		if (p == null) {
    			main.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
    			return false;
    		}
    		
        	String uuid = p.getUniqueId().toString();
    		Boolean isAllowedFlight = null;
    		
    		if (args.length > 0) {
    			if (args[0] == "on") isAllowedFlight = true;
    			if (args[0] == "off") isAllowedFlight = false;
    		}
    		
    		if (isAllowedFlight == null) {
    			isAllowedFlight = p.getAllowFlight();
    		
    			if (isAllowedFlight == true) {
    				//p.setAllowFlight(false);
    				// Add teleportation to ground
    				//p.setFlying(false);
    				isAllowedFlight = false;
    			}
    			else {
    				//p.setAllowFlight(true);
    				isAllowedFlight = true;
    			}
    		}
    		else {
    			//p.setAllowFlight(isAllowedFlight);  
    		}
    		
    		// PluginMessage
    		PMessage pm = new PMessage(main, "bungee.player.update.isAllowedFlight");
    		pm.put((p == null) ? "CONSOLE" : uuid);
    		pm.put((p == null) ? "CONSOLE" : uuid);
    		pm.put(isAllowedFlight ? "true" : "false");
    		pm.put("true");
    		pm.send(p);
    	}
    	
    	// Other
    	else {
    		
    	}
    
        return true;
    }
}
