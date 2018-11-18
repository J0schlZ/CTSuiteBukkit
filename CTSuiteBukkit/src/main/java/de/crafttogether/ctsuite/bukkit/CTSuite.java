package de.crafttogether.ctsuite.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariDataSource;

import de.crafttogether.ctsuite.bukkit.events.PlayerJoinListener;
import de.crafttogether.ctsuite.bukkit.handlers.PlayerHandler;

public class CTSuite extends JavaPlugin {	
    private static CTSuite instance;
    private HikariDataSource hikari;
    private Configuration config;
    private String tablePrefix;
    
    private PlayerHandler playerHandler;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        config = getConfig();

        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", config.get("MySQL.host"));
        hikari.addDataSourceProperty("port", config.get("MySQL.port"));
        hikari.addDataSourceProperty("databaseName", config.get("MySQL.database"));
        hikari.addDataSourceProperty("user", config.get("MySQL.user"));
        hikari.addDataSourceProperty("password", config.get("MySQL.password"));
        tablePrefix = config.getString("MySQL.prefix");

        playerHandler = new PlayerHandler(this);
        
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    public void onDisable() {
        if (hikari != null) {
        	try {
        		hikari.close();
        	}
        	catch (Exception ex) {
        		System.out.println(ex);
        	}
        }
    }

    public String getTablePrefix() {
    	return tablePrefix;
    }
    
    public PlayerHandler getPlayerHandler() {
    	return playerHandler;
    }
    
    public HikariDataSource getHikari() {
        return hikari;
    }
    
    public static CTSuite getInstance() {
        return instance;
    }
}