package de.crafttogether.ctsuite.bukkit.commands.teleport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

public class TPPosCommand implements TabExecutor {
    private CTSuite main;

    public TPPosCommand(CTSuite main) {
        this.main = main;
    }
    
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
        Player p = null;
        Player target = null;
        World world = null;
        String worldName = p.getWorld().getName();
        String serverName = p.getServer().getName();
        
        if (sender instanceof Player)
            p = Bukkit.getPlayer(((Player) sender).getUniqueId());

        if (args.length != 3 || !args[0].matches("[0-9.]+") || !args[1].matches("[0-9.]+") || !args[2].matches("[0-9.]+"))
        	return false;
        
        if (p == null) {
            this.main.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
            return true;
        }
        
        if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.tppos"))
            return true;

        if (args[4] != null)
        	serverName = args[4];
        
        if (args[3] != null)
        	worldName = args[3];
        
        String x = args[0];
        String y = args[1];
        String z = args[2];
        String pitch = (!args[5].matches("[0-9.]+")) ? null : args[5];
        String yaw = (!args[6].matches("[0-9.]+")) ? null : args[6];
        
        String strLocation = serverName + ":" + worldName + ":" + x + ":" + y + ":" + z + ":" + pitch + ":" + yaw;        
        world = Bukkit.getWorld(worldName);
        
        // Location is on same Server
        if (world != null && p.isOnline() && p.getServer().getName().equalsIgnoreCase(serverName)) {
        	Location loc = new Location(world, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
        	
        	if (pitch != null)	loc.setPitch(Float.parseFloat(pitch));
        	if (yaw != null)	loc.setYaw(Float.parseFloat(yaw));
        	
        	p.teleport(loc);
        }
        else {
        	PMessage pm = new PMessage(this.main, "bungee.player.cmd.tppos");
            pm.put(p.getUniqueId().toString());
            pm.put(strLocation);
            pm.put("true");
            pm.send(p);
        }
        
        return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {		
		if (cmd.getName().equalsIgnoreCase("gamemode") || cmd.getName().equalsIgnoreCase("gm")) {
			Player p = null;
			List<String> proposals = new ArrayList<String>();
			List<String> newList = new ArrayList<String>();
			
			if (sender instanceof Player)
				p = (Player) sender;
			
			if (p == null || main.getPlayerHandler().hasPermission(p, "ctsuite.command.tppos"))
				return null;

			switch (args.length) {
				case 1: proposals.add(""+ p.getLocation().getX()); break;
				case 2: proposals.add(""+ p.getLocation().getY()); break;
				case 3: proposals.add(""+ p.getLocation().getZ()); break;
				case 4: proposals.add(""+ p.getWorld().getName()); break;
				case 5: proposals.add(""+ p.getServer().getName()); break;
				case 6: proposals.add(""+ p.getLocation().getYaw()); break;
				case 7: proposals.add(""+ p.getLocation().getPitch()); break;
			}

			if (args[args.length -1].equals(""))
				newList = proposals;
			else {
				for (String value : proposals) {
					if (value.toLowerCase().startsWith(args[args.length -1].toLowerCase())) {
						newList.add(value);
					}
				}
			}
			
			return newList;
		}
		
		return null;
	}
}