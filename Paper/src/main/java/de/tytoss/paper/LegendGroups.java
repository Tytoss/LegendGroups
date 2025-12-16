package de.tytoss.paper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.paper.commands.LegendGroupCommand;
import de.tytoss.paper.configuration.ConfigurationManager;
import de.tytoss.paper.listener.ConnectionListener;
import de.tytoss.paper.listener.PacketListener;
import de.tytoss.paper.menu.MenuManager;
import de.tytoss.paper.menu.menus.input.*;
import de.tytoss.paper.messenger.PaperSync;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

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
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketListener(), PacketListenerPriority.NORMAL);

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

        PermissionOwnerRepository.loadGroups().subscribe( groups -> {
            Core.getInstance();
            groups.forEach(group -> Core.getInstance().getGroupManager().cache(group));
            this.getLogger().log(Level.INFO, "Loaded " + groups.size() + " groups");
        });
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "legendgroups:sync");

        PaperSync.syncScheduler();

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerAddGroupAnvil(), this);
        Bukkit.getPluginManager().registerEvents(new GroupCreateAnvil(), this);
        Bukkit.getPluginManager().registerEvents(new PermissionAddAnvil(), this);
        Bukkit.getPluginManager().registerEvents(new PrefixAnvil(), this);
        Bukkit.getPluginManager().registerEvents(new WeightAnvil(), this);

        getCommand("legendgroups").setExecutor(new LegendGroupCommand());

        MenuManager.setup(this.getServer());
    }

    @Override
    public void onDisable() {
    }
}
