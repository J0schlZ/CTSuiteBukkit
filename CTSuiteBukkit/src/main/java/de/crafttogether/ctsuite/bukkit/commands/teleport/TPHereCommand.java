package de.crafttogether.ctsuite.bukkit.commands.teleport;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import de.crafttogether.ctsuite.bukkit.CTSuite;

public class TPHereCommand implements TabExecutor {
    private CTSuite main;

    public TPHereCommand(CTSuite main) {
        this.main = main;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
}
