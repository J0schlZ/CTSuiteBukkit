package de.crafttogether.ctsuite.bukkit.commands;

import java.util.List;
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
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String st, final String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = Bukkit.getPlayer(((Player)sender).getUniqueId());
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
            final UUID uuid = p.getUniqueId();
            Boolean isAllowedFlight = null;
            if (argTrue) {
                isAllowedFlight = true;
            }
            if (argFalse) {
                isAllowedFlight = false;
            }
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
            else {
                p.setAllowFlight((boolean)isAllowedFlight);
            }
            final NetworkMessage nm = new NetworkMessage("player.cmd.fly");
            nm.put("targetName", p.getName());
            nm.put("senderUUID", uuid.toString());
            nm.put("fly", isAllowedFlight ? "on" : "off");
            nm.put("apply", false);
            nm.send("proxy");
        }
        else {
            Boolean isAllowedFlight2 = null;
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
                isAllowedFlight2 = true;
            }
            if (argFalse) {
                isAllowedFlight2 = false;
            }
            target = Bukkit.getPlayer(targetName);
            if (target != null && target.isOnline()) {
                if (argTrue) {
                    isAllowedFlight2 = true;
                }
                if (argFalse) {
                    isAllowedFlight2 = false;
                }
                if (isAllowedFlight2 == null) {
                    isAllowedFlight2 = target.getAllowFlight();
                    if (isAllowedFlight2) {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        isAllowedFlight2 = false;
                    }
                    else {
                        target.setAllowFlight(true);
                        isAllowedFlight2 = true;
                    }
                }
                else {
                    target.setAllowFlight((boolean)isAllowedFlight2);
                }
                applyViaBungee = false;
            }
            if (senderUUID.equals("CONSOLE")) {
                if (isAllowedFlight2 != null) {
                    this.plugin.getLogger().info("[CTSuite]: Fly-Mode " + (isAllowedFlight2 ? "enabled" : "disabled") + " for player" + targetName);
                }
                else {
                    this.plugin.getLogger().info("[CTSuite]: Fly-Mode toggled for player " + targetName);
                }
            }
            final NetworkMessage nm2 = new NetworkMessage("player.cmd.fly");
            nm2.put("targetName", targetName);
            nm2.put("senderUUID", senderUUID);
            nm2.put("fly", (isAllowedFlight2 != null) ? (isAllowedFlight2 ? "on" : "off") : "toggle");
            nm2.put("apply", applyViaBungee);
            nm2.send("proxy");
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias, final String[] args) {
        return null;
    }
}
