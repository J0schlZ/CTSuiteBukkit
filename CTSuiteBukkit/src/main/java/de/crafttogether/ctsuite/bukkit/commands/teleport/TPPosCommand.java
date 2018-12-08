package de.crafttogether.ctsuite.bukkit.commands.teleport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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

public class TPPosCommand implements TabExecutor {
    private CTSuite plugin;

    public TPPosCommand() {
        this.plugin = CTSuite.getInstance();
    }
    
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
        Player p = null;
        World world = null;
        
        if (sender instanceof Player)
            p = Bukkit.getPlayer(((Player) sender).getUniqueId());

        if (p == null) {
            this.plugin.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
            return true;
        }
        
        Double x = Double.NaN;
        Double y = Double.NaN;
        Double z = Double.NaN;
        String worldName = p.getWorld().getName();
        String serverName = Bukkit.getServerName();
        Float yaw = Float.NaN;
        Float pitch = Float.NaN;
        
        if (args.length < 3 || !args[0].matches("[0-9.-]+") || !args[1].matches("[0-9.-]+") || !args[2].matches("[0-9.-]+"))
        	return false;
        
        if (!plugin.getPlayerHandler().checkPermission(p, "ctsuite.command.tppos"))
            return true;

        if (args.length > 3 && args[3] != null)
        	worldName = args[3];
        
        if (args.length > 4 && args[4] != null)
        	serverName = args[4];
        
        try {
	        x = Double.parseDouble(args[0]);
	        y = Double.parseDouble(args[1]);
	        z = Double.parseDouble(args[2]);
        } catch (Exception e) { }
        
        if (x.equals(Double.NaN) || y.equals(Double.NaN) || z.equals(Double.NaN))
        	return false;

        if (args.length > 5) {
        	try {
        	yaw = Float.parseFloat(args[6]);
        	} catch (Exception e) { }
        	if (yaw.equals(Float.NaN)) return false;
        }

        if (args.length > 6) {
        	try {
        	pitch = Float.parseFloat(args[6]);
        	} catch (Exception e) { }
        	if (pitch.equals(Float.NaN)) return false;
        }

        if (yaw.equals(Float.NaN))
        	yaw = p.getLocation().getYaw();
        
        if (pitch.equals(Float.NaN))
        	pitch = p.getLocation().getPitch();
              
        world = Bukkit.getWorld(worldName);
        Location loc = new Location(world, x, y, z, yaw, pitch);
        
        System.out.println(loc);
        System.out.println("isOnline: " + p.isOnline());
        System.out.println("p.getServer: " + Bukkit.getServerName());
        System.out.println("serverName: " + serverName);
        
        // Location is on same Server
        if (world != null && p.isOnline() && Bukkit.getServerName().equalsIgnoreCase(serverName))
    		p.teleport(loc);
        else {
        	NetworkMessage nm = new NetworkMessage("player.cmd.tppos");
        	nm.put("uuid", p.getUniqueId());
        	nm.put("x", x);
        	nm.put("y", y);
        	nm.put("z", z);
        	nm.put("world", worldName);
        	nm.put("server", serverName);
        	nm.put("yaw", "" + yaw);
        	nm.put("pitch", "" + pitch);
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
						if (!entry.getKey().equalsIgnoreCase(args[3]) || !plugin.getPlayerHandler().hasPermission(p, "ctsuite.server." + entry.getKey())) continue;
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