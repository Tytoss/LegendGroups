package de.tytoss.paper;

import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.core.manager.GroupManager;
import de.tytoss.paper.commands.TestCommand;
import de.tytoss.paper.configuration.ConfigurationManager;
import de.tytoss.paper.listener.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class LegendGroups extends JavaPlugin {

    private static LegendGroups instance;

    public static LegendGroups getInstance() {
        return instance;
    }

    public static ConfigurationManager configManager;

    @Override
    public void onLoad() {
        instance = this;

        configManager = new ConfigurationManager(this);

        try {
            configManager.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String host = configManager.get().node("database", "host").getString();
        String port = configManager.get().node("database", "port").getString();
        String database = configManager.get().node("database", "database").getString();
        String username = configManager.get().node("database", "username").getString();
        String password = configManager.get().node("database", "password").getString();

        DatabaseManager databaseManager = new DatabaseManager();

        databaseManager.init(host, Integer.parseInt(port), database, username, password);

        Core.getInstance();
    }

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "legendgroups:sync");

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);

        getCommand("abc").setExecutor(new TestCommand());
    }

    @Override
    public void onDisable() {
    }
}
