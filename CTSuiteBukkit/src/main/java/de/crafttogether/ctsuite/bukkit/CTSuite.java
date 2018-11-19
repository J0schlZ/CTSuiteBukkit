package de.crafttogether.ctsuite.bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariDataSource;

import de.crafttogether.ctsuite.bukkit.events.PlayerJoinListener;
import de.crafttogether.ctsuite.bukkit.handlers.PlayerHandler;
import de.crafttogether.ctsuite.bukkit.util.PMessageListener;
import net.milkbowl.vault.chat.Chat;

public class CTSuite extends JavaPlugin {	
    private static CTSuite instance;
    private HikariDataSource hikari;
    private Configuration config;
    private String tablePrefix;
    private Chat chat;
    private boolean vaultLoaded;
    private Logger log;
    
    private PlayerHandler playerHandler;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        config = getConfig();
        log = Bukkit.getLogger();

        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", config.get("MySQL.host"));
        hikari.addDataSourceProperty("port", config.get("MySQL.port"));
        hikari.addDataSourceProperty("databaseName", config.get("MySQL.database"));
        hikari.addDataSourceProperty("user", config.get("MySQL.user"));
        hikari.addDataSourceProperty("password", config.get("MySQL.password"));
        tablePrefix = config.getString("MySQL.prefix");
        
        // Chef if Vault is loaded
        vaultLoaded = (getServer().getPluginManager().getPlugin("Vault") != null) ? true : false;
        if (vaultLoaded) {
            RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
            if (chatProvider != null)
                chat = chatProvider.getProvider();
        }
        else {
        	getLog().log(Level.WARNING, "Couln't find Vault.");
        }
        
        playerHandler = new PlayerHandler(this);
        
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        
        getServer().getMessenger().registerOutgoingPluginChannel(this, "ctsuite:bungee");
        getServer().getMessenger().registerIncomingPluginChannel(this, "ctsuite:bukkit", new PMessageListener(this));
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
    
    public Logger getLog() {
    	return log;
    }
    
    public Chat getChat() {
    	return chat;
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