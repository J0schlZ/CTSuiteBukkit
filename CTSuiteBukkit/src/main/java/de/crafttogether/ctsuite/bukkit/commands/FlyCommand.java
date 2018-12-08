package de.crafttogether.ctsuite.bukkit.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.messaging.NetworkMessage;

public class FlyCommand implements TabExecutor
{
    private CTSuite plugin;
    
    public FlyCommand() {
        this.plugin = CTSuite.getInstance();
    }
    
    public boolean onCommand( CommandSender sender,  Command cmd,  String st,  String[] args) {
        Player p = null;
        
        if (sender instanceof Player) {
            p = Bukkit.getPlayer(((Player) sender).getUniqueId());
        }
        
        Boolean argTrue = args.length == 1 && (args[0].equals("on") || args[0].equals("yes") || args[0].equals("true"));
        Boolean argFalse = args.length == 1 && (args[0].equals("off") || args[0].equals("no") || args[0].equals("false"));
        
        if (args.length < 1 || argTrue || argFalse) {
            if (p == null) {
                this.plugin.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
                return true;
            }
            if (!this.plugin.getPlayerHandler().checkPermission(p, "ctsuite.command.fly")) {
                return true;
            }
            
            UUID uuid = p.getUniqueId();
            Boolean isAllowedFlight = null;
            
            if (argTrue)
                isAllowedFlight = true;
            
            if (argFalse)
                isAllowedFlight = false;
            
            if (isAllowedFlight == null) {
                isAllowedFlight = p.getAllowFlight();
                if (isAllowedFlight) {
                    p.setAllowFlight(false);
                    p.setFlying(false);
                    isAllowedFlight = false;
                }
                else {
                    p.setAllowFlight(true);
                    isAllowedFlight = true;
                }
            }
            else
                p.setAllowFlight((boolean) isAllowedFlight);
            
            NetworkMessage nm = new NetworkMessage("player.cmd.fly");
            nm.put("targetName", p.getName());
            nm.put("senderUUID", uuid.toString());
            nm.put("fly", isAllowedFlight ? "on" : "off");
            nm.put("apply", false);
            nm.send("proxy");
        }
        else {
            Boolean isAllowedFlight = null;
            Boolean applyViaBungee = true;
            String senderUUID = "CONSOLE";
            String targetName = args[0];
            Player target = null;
            
            if (p != null) {
                senderUUID = p.getUniqueId().toString();
                if (!this.plugin.getPlayerHandler().checkPermission(p, "ctsuite.command.fly.others")) {
                    return true;
                }
            }
            
            argTrue = (args.length > 1 && (args[0].equals("on") || args[0].equals("yes") || args[0].equals("true")));
            argFalse = (args.length > 1 && (args[0].equals("off") || args[0].equals("no") || args[0].equals("false")));
            
            if (argTrue || argFalse) {
                targetName = args[1];
            }
            if (argTrue) {
                isAllowedFlight = true;
            }
            if (argFalse) {
                isAllowedFlight = false;
            }
            
            target = Bukkit.getPlayer(targetName);
            if (target != null && target.isOnline()) {
                if (argTrue) {
                    isAllowedFlight = true;
                }
                if (argFalse) {
                    isAllowedFlight = false;
                }
                if (isAllowedFlight == null) {
                    isAllowedFlight = target.getAllowFlight();
                    if (isAllowedFlight) {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        isAllowedFlight = false;
                    }
                    else {
                        target.setAllowFlight(true);
                        isAllowedFlight = true;
                    }
                }
                else {
                    target.setAllowFlight((boolean)isAllowedFlight);
                }
                applyViaBungee = false;
            }
            
            if (senderUUID.equals("CONSOLE")) {
                if (isAllowedFlight != null) {
                    this.plugin.getLogger().info("[CTSuite]: Fly-Mode " + (isAllowedFlight ? "enabled" : "disabled") + " for player" + targetName);
                }
                else {
                    this.plugin.getLogger().info("[CTSuite]: Fly-Mode toggled for player " + targetName);
                }
            }
            
            NetworkMessage nm = new NetworkMessage("player.cmd.fly");
            nm.put("targetName", targetName);
            nm.put("senderUUID", senderUUID);
            nm.put("fly", (isAllowedFlight != null) ? (isAllowedFlight ? "on" : "off") : "toggle");
            nm.put("apply", applyViaBungee);
            nm.send("proxy");
        }
        return true;
    }
    
    public List<String> onTabComplete( CommandSender sender,  Command cmd,  String alias,  String[] args) {
        if (cmd.getName().equalsIgnoreCase("fly")) {
            Player p = null;
            
            List<String> newList = new ArrayList<String>();
            List<String> proposals = new ArrayList<String>();
            
            Boolean hasPermFly = false;
            Boolean hasPermFlyOthers = false;
            
            if (sender instanceof Player)
                p = (Player)sender;
            
            if (p == null || plugin.getPlayerHandler().hasPermission(p, "ctsuite.command.fly"))
                hasPermFly = true;
            
            if (p == null || plugin.getPlayerHandler().hasPermission(p, "ctsuite.command.fly.others"))
                hasPermFlyOthers = true;
            
            if (args.length == 1 && hasPermFly) {
                proposals.add("on");
                proposals.add("off");
                
                if (hasPermFlyOthers) {
                    for (Entry<UUID, HashMap<String, Object>> entry : plugin.getPlayerHandler().onlinePlayers.entrySet()) {
                    	String playerName = (String) entry.getValue().get("name");
                    	if (p != null && p.getName().equalsIgnoreCase(playerName))
                    		continue;
                    	
                        proposals.add((String) entry.getValue().get("name"));
                    }
                }
            }

            if (args.length > 1 && args.length < 3 && hasPermFlyOthers) {
                Boolean argTrue = args[0].equals("on") || args[0].equals("yes") || args[0].equals("true");
                Boolean argFalse = args[0].equals("off") || args[0].equals("no") || args[0].equals("false");
                
                if (argTrue || argFalse) {
                    for (Entry<UUID, HashMap<String, Object>> entry : plugin.getPlayerHandler().onlinePlayers.entrySet()) {
                    	String playerName = (String) entry.getValue().get("name");
                    	if (p != null && p.getName().equalsIgnoreCase(playerName))
                    		continue;
                    	
                        proposals.add((String) entry.getValue().get("name"));
                    }
                }
            }
            
            if (args.length < 1 || args[args.length - 1].equals(""))
                newList = proposals;
            else {
                for (String value : proposals) {
                    if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        newList.add(value);
                }
            }
            return newList;
        }
        
        return null;
    }
}
