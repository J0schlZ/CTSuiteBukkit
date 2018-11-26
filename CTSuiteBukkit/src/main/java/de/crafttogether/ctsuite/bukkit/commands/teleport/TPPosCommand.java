package de.crafttogether.ctsuite.bukkit.commands.teleport;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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
        
        if (sender instanceof Player)
            p = Bukkit.getPlayer(((Player) sender).getUniqueId());

        if (args.length != 1)
        	return false;
        
        if (p == null) {
            this.main.getLogger().log(Level.INFO, "[CTSuite]: This command can't performed by Console");
            return true;
        }
        
        if (!this.main.getPlayerHandler().checkPermission(p, "ctsuite.command.tppos"))
            return true;
        
        target = Bukkit.getPlayer(args[0]);
     
        if (target != null && target.isOnline())
            p.teleport(target.getLocation());
        else {
        	PMessage pm = new PMessage(this.main, "bungee.player.cmd.tppos");
            pm.put(p.getName());
            pm.put(args[0]);
            pm.put("true");
            pm.send(p);
        }
        
        return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {		
		return null;
	}
}