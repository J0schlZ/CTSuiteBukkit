package de.crafttogether.ctsuite.bukkit;

import com.zaxxer.hikari.HikariDataSource;
import de.crafttogether.ctsuite.bukkit.Events;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CTSuite extends JavaPlugin {
	
    private static CTSuite instance;
    private HikariDataSource hikari;
    private Configuration config;
    private HashMap < UUID, CTPlayer > players;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.config = getConfig();

        this.hikari = new HikariDataSource();
        this.hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        this.hikari.addDataSourceProperty("serverName", this.config.get("MySQL.host"));
        this.hikari.addDataSourceProperty("port", this.config.get("MySQL.port"));
        this.hikari.addDataSourceProperty("databaseName", this.config.get("MySQL.database"));
        this.hikari.addDataSourceProperty("user", this.config.get("MySQL.user"));
        this.hikari.addDataSourceProperty("password", this.config.get("MySQL.password"));

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
    }

    public void onDisable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    public static CTSuite getInstance() {
        return instance;
    }

    public HikariDataSource getHikari() {
        return this.hikari;
    }
}