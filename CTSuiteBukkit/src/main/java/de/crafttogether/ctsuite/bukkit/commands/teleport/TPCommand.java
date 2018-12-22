package de.crafttogether.ctsuite.bukkit.commands.teleport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;
import de.crafttogether.ctsuite.bukkit.util.CTLocation;

public class TPCommand implements TabExecutor {
    private CTSuite plugin;

    public TPCommand() {
        this.plugin = CTSuite.getInstance();
    }
    
	@Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String st, String[] args) {
        Player sender = null;
        String senderUUID = "CONSOLE";
        Player target = null;
        String targetName = null;
        Player player = null;
        String playerName = null;
        
        if (commandSender instanceof Player) {
            sender = (Player) commandSender;
            senderUUID = sender.getUniqueId().toString();
        }

        if (args.length == 1) {
        	if (!plugin.getPlayerHandler().checkPermission(commandSender, "ctsuite.command.tp"))
        		return true;
        	
        	if (sender == null) {
                this.plugin.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
                return true;
            }
        	
        	player = sender;
        	playerName = sender.getName();
        	targetName = args[0];
        	target = Bukkit.getPlayer(targetName);
        }
        else if (args.length == 2) {
        	if (!plugin.getPlayerHandler().checkPermission(commandSender, "ctsuite.command.tp.other"))
        		return true;
        	
        	playerName = args[0];
        	targetName = args[1];
        	player = Bukkit.getPlayer(playerName);
        	target = Bukkit.getPlayer(targetName);
        }
        else
        	return false;
        
        if (player != null && player.isOnline() && target != null && target.isOnline())
            player.teleport(target.getLocation());
        else {
	    	NetworkMessage nm = new NetworkMessage("player.teleport.player");
	    	nm.put("senderUUID", senderUUID);
	    	nm.put("playerName", playerName);
	    	nm.put("targetName", targetName);
	    	nm.send("proxy");
        }
        
        return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {		
		if (cmd.getName().equalsIgnoreCase("tppos")) {
			Player p = null;
			List<String> proposals = new ArrayList<String>();
			List<String> newList = new ArrayList<String>();
			
			if (sender instanceof Player)
				p = (Player) sender;
			
			if (p == null || !plugin.getPlayerHandler().hasPermission(p, "ctsuite.command.tppos"))
				return null;
			
			if (args.length > 0 && args.length < 3) {
				for (Entry<UUID, HashMap<String, Object>> entry : plugin.getPlayerHandler().onlinePlayers.entrySet()) {
					String playerName = (String) entry.getValue().get("name");
					if (playerName.equalsIgnoreCase(p.getName())) continue;
					proposals.add(playerName);
				}
			}
			
			if (args.length < 1 || args[args.length -1].equals(""))
				newList = proposals;
			else {
				for (String value : proposals) {
					if (value.toLowerCase().startsWith(args[args.length -1].toLowerCase()))
						newList.add(value);
				}
			}
			return newList;
		}
		return null;
	}
}