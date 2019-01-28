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

public class TPPosCommand implements TabExecutor {
    private CTSuite plugin;

    public TPPosCommand() {
        this.plugin = CTSuite.getInstance();
    }
    
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
		String targetName = null;
        Player target = null;
        Player p = null;
        
        if (sender instanceof Player)
            p = (Player) sender;

        Double x = Double.NaN;
        Double y = Double.NaN;
        Double z = Double.NaN;
        String worldName = null;
        String serverName = null;
        Float yaw = Float.NaN;
        Float pitch = Float.NaN;

        if (!plugin.getPlayerHandler().checkPermission(sender, "ctsuite.command.tppos"))
            return true;
        
        if (args.length > 6 && !plugin.getPlayerHandler().checkPermission(sender, "ctsuite.command.tppos.others"))
            return true;
        
        if (p != null)
        	worldName = p.getWorld().getName();
        
        if (args.length < 3 || !args[0].matches("[0-9.-]+") || !args[1].matches("[0-9.-]+") || !args[2].matches("[0-9.-]+"))
        	return false;

        try {
	        x = Double.parseDouble(args[0]);
	        y = Double.parseDouble(args[1]);
	        z = Double.parseDouble(args[2]);
        } catch (Exception e) { }

        if (x.equals(Double.NaN) || y.equals(Double.NaN) || z.equals(Double.NaN))
        	return false;
        
        if (args.length > 3)
        	worldName = args[3];

        if (args.length > 4)
        	serverName = args[4];
        
        if (args.length > 5)
        	targetName = args[5];
        else {
        	if (p == null) {
        		plugin.getLogger().info("This command can't performed by Console");
        		return false;
        	}
        	else {
        		targetName = p.getName();
        	}
        }
        
        target = Bukkit.getPlayer(targetName);
        
        if (target == null)
        	return false;

        for (int i = 0; i < args.length; i++) {
        	try {
        		if (args[i].startsWith("-pitch ")) {
        			pitch = Float.parseFloat(args[i+1]);
        			if (pitch.equals(Float.NaN)) return false;
        		}
        		if (args[i].startsWith("-yaw ")) {
        			yaw = Float.parseFloat(args[i+1]);
        			if (yaw.equals(Float.NaN)) return false;
        		}
        	} catch (Exception e) { }
        }
        
        if (plugin.getWorldHandler().worlds.containsKey(worldName))
        	serverName = plugin.getWorldHandler().worlds.get(worldName);
        
        target = Bukkit.getPlayer(targetName);
        World world = Bukkit.getWorld(worldName);
        Location loc = null;
        
    	if (!pitch.isNaN() && !yaw.isNaN())
    		loc = new Location(world, x, y, z, pitch, yaw);
    	else
    		loc = new Location(world, y, y, z);
        
        if (world != null && target != null && target.isOnline() && Bukkit.getServerName().equalsIgnoreCase(serverName))       	
    		target.teleport(loc);
        else {
        	CTLocation ctLoc = new CTLocation(loc, worldName, serverName);
        	NetworkMessage nm = new NetworkMessage("player.teleport.location");
        	nm.put("targetName", targetName);
        	nm.put("senderUUID", (p == null) ? "CONSOLE" : p.getUniqueId());
        	nm.put("location", ctLoc.toString());
        	nm.send("proxy");
        }
        
        return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command cmd, String alias, String[] args) {
		Player sender = null;
		
		if (cmd.getName().equalsIgnoreCase("tppos")) {
			Player p = null;
			List<String> proposals = new ArrayList<String>();
			List<String> newList = new ArrayList<String>();
			
			if (commandSender instanceof Player)
				sender = (Player) commandSender;
			
			if (!plugin.getPlayerHandler().hasPermission(p, "ctsuite.command.tppos"))
				return null;
			
			switch (args.length) {
				case 1: proposals.add(""+ Math.round(p.getLocation().getX() *10) / 10.0); break;
				case 2: proposals.add(""+ Math.round(p.getLocation().getY() *10) / 10.0); break;
				case 3: proposals.add(""+ Math.round(p.getLocation().getZ() *10) / 10.0); break;
				case 4:
					for (Entry<String, String> entry : plugin.getWorldHandler().worlds.entrySet()) {
						if (!plugin.getPlayerHandler().hasPermission(p, "ctsuite.worlds." + entry.getKey())) continue;
						proposals.add(entry.getKey());
					}
					break;
				case 5:
					for (Entry<String, String> entry : plugin.getWorldHandler().worlds.entrySet()) {
						if (!entry.getKey().equalsIgnoreCase(args[3]) || !plugin.getPlayerHandler().hasPermission(sender, "ctsuite.server." + entry.getKey())) continue;
						proposals.add(entry.getValue());
						break;
					}
					if (proposals.size() < 1) {
						proposals.add(Bukkit.getServerName());
						for (String server : plugin.getWorldHandler().server) {
							if (server.equalsIgnoreCase("proxy") || !plugin.getPlayerHandler().hasPermission(p, "ctsuite.server." + server)) continue;
							proposals.add(server);
						}
					}
					break;
				case 6: proposals.add(""+ Math.round(p.getLocation().getYaw() *10) / 10.0); break;
				case 7: proposals.add(""+ Math.round(p.getLocation().getPitch() *10) / 10.0); break;
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