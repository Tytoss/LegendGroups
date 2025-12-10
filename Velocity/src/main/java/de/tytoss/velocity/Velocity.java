package de.tytoss.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.velocity.configuration.ConfigurationManager;
import de.tytoss.velocity.listener.PermissionSetupListener;
import de.tytoss.velocity.listener.PlayerDisconnectListener;
import de.tytoss.velocity.listener.PlayerJoinListener;
import de.tytoss.velocity.messenger.PluginMessageHandler;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "velocity",
    name = "Velocity"
)
public class Velocity {

    public static ProxyServer server;
    public static Logger logger;
    public static ConfigurationManager configManager;
    private final Path dataDirectory;
    private final ChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.from("legendgroups:sync");
    private static Velocity instance;

    @Inject
    public Velocity(@DataDirectory Path dataDirectory, ProxyServer server, Logger logger) {
        Velocity.server = server;
        Velocity.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    public static Velocity getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        configManager = new ConfigurationManager(dataDirectory);

        try {
            configManager.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String host = configManager.get().node("database", "host").getString();
        String port = configManager.get().node("database", "port").getString();
        String database = configManager.get().node("database", "database").getString();
        String username = configManager.get().node("database", "username").getString();
        String password = configManager.get().node("database", "password").getString();

        DatabaseManager databaseManager = new DatabaseManager();

        databaseManager.init(host, Integer.parseInt(port), database, username, password);

        PermissionOwnerRepository.loadGroups().subscribe( groups -> {
            Core.getInstance();
            groups.forEach(group -> Core.getInstance().getGroupManager().cache(group));
        });

        server.getChannelRegistrar().register(channelIdentifier);

        server.getEventManager().register(this, new PluginMessageHandler());
        server.getEventManager().register(this, new PlayerJoinListener());
        server.getEventManager().register(this, new PermissionSetupListener());
        server.getEventManager().register(this, new PlayerDisconnectListener());
    }
}
