package de.crafttogether.ctsuite.bukkit.commands;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.crafttogether.ctsuite.bukkit.CTSuite;
import de.crafttogether.ctsuite.bukkit.util.PMessage;

public class FlyCommand
implements CommandExecutor {
    private CTSuite main;

    public FlyCommand(CTSuite main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
        Player p = null;
        
        if ((sender instanceof Player)) {
            p = Bukkit.getPlayer(((Player) sender).getUniqueId());
        }
        
        Boolean argTrue = Boolean.valueOf((args.length > 0) && ((args[0].equals("on")) || (args[0].equals("yes")) || (args[0].equals("true"))));
        Boolean argFalse = Boolean.valueOf((args.length > 0) && ((args[0].equals("off")) || (args[0].equals("no")) || (args[0].equals("false"))));
        
        if ((args.length < 1) || (argTrue) || (argFalse)) {
            if (p == null) {
                this.main.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
                return true;
            }
            if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.fly")) {
                return true;
            }
            
            String uuid = p.getUniqueId().toString();
            Boolean isAllowedFlight = null;
            
            if (args.length > 0) {
                if (argTrue) {
                    isAllowedFlight = true;
                }
                if (argFalse) {
                    isAllowedFlight = false;
                }
            }
            
            if (isAllowedFlight == null) {
                isAllowedFlight = Boolean.valueOf(p.getAllowFlight());
                
                if (isAllowedFlight == true) {
                    p.setAllowFlight(false);
                    p.setFlying(false);
                    isAllowedFlight = false;
                } else {
                    p.setAllowFlight(true);
                    isAllowedFlight = true;
                }
            } else {
                p.setAllowFlight(isAllowedFlight);
            }
            
            PMessage pm = new PMessage(this.main, "bungee.player.update.isAllowedFlight");
            pm.put(p.getName());
            pm.put(uuid);
            pm.put(isAllowedFlight ? "on" : "off");
            pm.put("false");
            pm.send(p);
        } else {
            Boolean isAllowedFlight = null;
            Boolean applyViaBungee = true;
            String senderUUID = "CONSOLE";
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);
            
            if (p != null) {
                senderUUID = p.getUniqueId().toString();
                if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.fly.others")) {
                    return true;
                }
            }
            
            if ((target != null) && (target.isOnline())) {                
                argTrue = Boolean.valueOf((args.length > 1) && ((args[1].equals("on")) || (args[1].equals("yes")) || (args[1].equals("true"))));
                argFalse = Boolean.valueOf((args.length > 1) && ((args[1].equals("off")) || (args[1].equals("no")) || (args[1].equals("false"))));
                
                if (args.length > 1) {
                    if (argTrue) {
                        isAllowedFlight = true;
                    }
                    if (argFalse) {
                        isAllowedFlight = false;
                    }
                }
                
                if (isAllowedFlight == null) {
                    isAllowedFlight = Boolean.valueOf(target.getAllowFlight());
                    
                    if (isAllowedFlight == true) {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        isAllowedFlight = false;
                    } else {
                        target.setAllowFlight(true);
                        isAllowedFlight = true;
                    }
                } else {
                    target.setAllowFlight(isAllowedFlight);
                }
                
                applyViaBungee = false;
            }
            
            if (senderUUID.equals("CONSOLE")) {
                this.main.getLogger().info("[CTSuite]: Fly-Mode für Spieler " + targetName + " " + (isAllowedFlight ? "aktiviert" : "deaktiviert"));
            }
            
            PMessage pm = new PMessage(this.main, "bungee.player.update.isAllowedFlight");
            pm.put(targetName);
            pm.put(senderUUID);
            pm.put(isAllowedFlight != null ? (isAllowedFlight ? "on" : "off") : "toggle");
            pm.put(applyViaBungee ? "true" : "false");
            pm.send(p);
        }
        return true;
    }
}