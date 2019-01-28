package de.crafttogether.ctsuite.bukkit;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.zaxxer.hikari.HikariDataSource;

import de.crafttogether.ctsuite.bukkit.commands.FlyCommand;
import de.crafttogether.ctsuite.bukkit.commands.GamemodeCommand;
import de.crafttogether.ctsuite.bukkit.commands.teleport.TPCommand;
import de.crafttogether.ctsuite.bukkit.commands.teleport.TPPosCommand;
import de.crafttogether.ctsuite.bukkit.events.PlayerListener;
import de.crafttogether.ctsuite.bukkit.events.WorldListener;
import de.crafttogether.ctsuite.bukkit.handlers.PlayerHandler;
import de.crafttogether.ctsuite.bukkit.handlers.WorldHandler;
import de.crafttogether.ctsuite.bukkit.messaging.adapter.Sockets4MC;
import net.milkbowl.vault.chat.Chat;

public class CTSuite extends JavaPlugin
{
    private static CTSuite plugin;
    private HikariDataSource hikari;
    private Configuration config;
    private MultiverseCore multiverse;
    private Chat chat;
    private String tablePrefix;
    private String messagingService;
    private boolean vaultLoaded;
    private boolean MVLoaded;
    private PlayerHandler playerHandler;
    private WorldHandler worldHandler;
    
    public void onEnable() {
        CTSuite.plugin = this;
        		
        this.saveDefaultConfig();
        this.config = this.getConfig();
        
        this.hikari = new HikariDataSource();
        this.hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        this.hikari.addDataSourceProperty("serverName", this.config.get("MySQL.host"));
        this.hikari.addDataSourceProperty("port", this.config.get("MySQL.port"));
        this.hikari.addDataSourceProperty("databaseName", this.config.get("MySQL.database"));
        this.hikari.addDataSourceProperty("user", this.config.get("MySQL.user"));
        this.hikari.addDataSourceProperty("password", this.config.get("MySQL.password"));
        
        this.messagingService = "Sockets4MC";
        this.tablePrefix = this.config.getString("MySQL.prefix");
        
        this.loadPlugins();
        
        final String messagingService = this.messagingService;
        switch (messagingService) {
            case "Sockets4MC": {
                new Sockets4MC();
                break;
            }
        }
        
        this.playerHandler = new PlayerHandler();
        this.worldHandler = new WorldHandler();
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        
        this.registerCommand("fly", new FlyCommand());
        this.registerCommand("gamemode", new  GamemodeCommand());

        this.registerCommand("tppos", new TPPosCommand());
        this.registerCommand("tp", new TPCommand());
        //this.registerCommand("tphere", new TPHereCommand());
        //this.registerCommand("tpa", new TPACommand());
        //this.registerCommand("tpahere", new TPAHereCommand());
        //this.registerCommand("tpaccept", new TPAcceptCommand());
        //this.registerCommand("tpdeny", new TPDenyCommand());
        
        this.playerHandler.readPlayersFromDB();
    }
    
    public void onDisable() {
        if (this.hikari != null) {
            try {
                this.hikari.close();
            }
            catch (Exception ex) {}
        }
    }
    
    public void registerCommand(final String cmd, final TabExecutor executor) {
        this.getCommand(cmd).setExecutor(executor);
        this.getCommand(cmd).setTabCompleter(executor);
    }
    
    private void loadPlugins() {
        PluginManager pm = this.getServer().getPluginManager();
        Plugin plugin = null;
        
        this.vaultLoaded = (pm.getPlugin("Vault") != null);
        if (this.vaultLoaded) {
        	RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
            if (chatProvider != null) {
                this.chat = chatProvider.getProvider();
            }
        }
        else
            this.getLogger().warning("Couln't find Vault.");
        
        this.MVLoaded = (pm.getPlugin("Multiverse-Core") != null);
        if (this.MVLoaded) {
            plugin = pm.getPlugin("Multiverse-Core");
            if (plugin instanceof MultiverseCore)
                this.multiverse = (MultiverseCore) plugin;
            else
                this.getLogger().warning("Couln't find Multiverse-Core.");
        }
    }
    
    public PlayerHandler getPlayerHandler() {
        return this.playerHandler;
    }
    
    public WorldHandler getWorldHandler() {
        return this.worldHandler;
    }
    
    public Connection getMySQLConnection() {
        try {
            return this.hikari.getConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public MultiverseCore getMultiverseCore() {
        return this.multiverse;
    }
    
    public Chat getChat() {
        return this.chat;
    }
    
    public String getTablePrefix() {
        return this.tablePrefix;
    }
    
    public String getMessagingService() {
        return this.messagingService;
    }
    
    public static CTSuite getInstance() {
        return CTSuite.plugin;
    }
}