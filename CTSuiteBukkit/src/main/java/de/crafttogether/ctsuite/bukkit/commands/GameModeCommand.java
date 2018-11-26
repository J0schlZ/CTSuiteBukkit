package de.crafttogether.ctsuite.bukkit.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

public class GameModeCommand implements TabExecutor {
    private CTSuite main;
    private HashMap<Integer, String> validModes;
    
    public GameModeCommand(CTSuite main) {
        this.main = main;
        
        this.validModes = new HashMap<Integer, String>();
        this.validModes.put(1, "SURVIVAL");
        this.validModes.put(2, "CREATIVE");
        this.validModes.put(3, "ADVENTURE");
        this.validModes.put(4, "SPECTATOR");
    }

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
        Player p = null;
        GameMode gameMode = null;
        
        if (sender instanceof Player)
            p = Bukkit.getPlayer(((Player) sender).getUniqueId());

        if (args.length < 1)
        	return false;
        
        try {
        	gameMode = GameMode.valueOf(args[0].toUpperCase());
        } catch (Exception e) { }
        
        if (gameMode == null) {
	        switch (Integer.parseInt(args[0])) {
	        	case 0: gameMode = GameMode.SURVIVAL; break;
	        	case 1: gameMode = GameMode.CREATIVE; break;
	        	case 2: gameMode = GameMode.ADVENTURE; break;
	        	case 3: gameMode = GameMode.SPECTATOR; break;
        	}
        }
        
        if (gameMode == null)
        	return false;
        
        if (args.length == 1) {
            if (p == null) {
                this.main.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
                return true;
            }
            
            if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.gamemode"))
                return true;
            
            if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.gamemode." + gameMode.toString()))
                return true;
            
            String uuid = p.getUniqueId().toString();
            p.setGameMode(gameMode);
            
            PMessage pm = new PMessage(this.main, "bungee.player.cmd.gamemode");
            pm.put(p.getName());
            pm.put(uuid);
            pm.put(gameMode.toString());
            pm.put("false");
            pm.send(p);
        }
        
        if (args.length == 2) {
            Boolean applyViaBungee = true;
            String senderUUID = "CONSOLE";
            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);
            
            if (p != null) {
                senderUUID = p.getUniqueId().toString();
                if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.gamemode.others"))
                    return true;
                
                if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.gamemode.others." + gameMode.toString()))
                    return true;
            }
            
            if ((target != null) && (target.isOnline())) {                
            	target.setGameMode(gameMode);
                applyViaBungee = false;
            }
            
            if (senderUUID.equals("CONSOLE"))
            	this.main.getLogger().info("[CTSuite]: Set Gamemode '" + gameMode.toString() + "' for player " + targetName);
            
            PMessage pm = new PMessage(this.main, "bungee.player.cmd.gamemode");
            pm.put(targetName);
            pm.put(senderUUID);
            pm.put(gameMode.toString());
            pm.put(applyViaBungee ? "true" : "false");
            pm.send((p == null) ? null : p);
        }
        
        return true;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {		
		if (cmd.getName().equalsIgnoreCase("gamemode") || cmd.getName().equalsIgnoreCase("gm")) {
			Player p = null;
			List<String> newList = new ArrayList<String>();
			List<String> proposals = new ArrayList<String>();
			
			Boolean hasPermGm = false;
			Boolean hasPermGmOthers = false;
			
			if (sender instanceof Player)
				p = (Player) sender;
			
			if (p == null || main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode"))
				hasPermGm = true;
			
			if (p == null || main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.others"))
				hasPermGmOthers = true;
		
			if ((args.length == 1) && hasPermGm) {
				if (main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.survival")
				 || main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.others.spectator"))
					proposals.add("survival");
				if (main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.creative")
				 || main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.others.spectator"))
					proposals.add("creative");
				if (main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.adventure")
				 || main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.others.spectator"))
					proposals.add("adventure");
				if (main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.spectator")
				 || main.getPlayerHandler().hasPermission(p, "ctsuite.command.gamemode.others.spectator"))
					proposals.add("spectator");
			}
			
			if (args.length == 2 && hasPermGmOthers) {
				for (HashMap<String, String> player : main.getPlayerHandler().bungeeOnlinePlayers.values())
					proposals.add(player.get("name"));
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