package de.tytoss.paper.listener;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.permissible.PermissionInjection;
import de.tytoss.paper.prefix.PrefixManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Core.getInstance().getPlayerManager().load(player.getUniqueId()).subscribe(permissionOwner -> {
            PermissionInjection.inject(player, permissionOwner);

            PermissionPlayer permissionPlayer = (PermissionPlayer) permissionOwner;

            Component joinMessage = Component.text("§8[§a+§8]§r " + PrefixManager.resolvePrefix(permissionPlayer).replace("&", "§") + "§r" + permissionPlayer.getName());

            event.joinMessage(joinMessage);

            PrefixManager.update((PermissionPlayer) permissionOwner);

            Bukkit.getScheduler().runTask(LegendGroups.getInstance(), () -> {
                for (PermissionOwner other : Core.getInstance().getPlayerManager().getAll()) {
                    if (!other.getId().equals(permissionOwner.getId())) {
                        PrefixManager.update((PermissionPlayer) other);
                    }
                }
            });
        });

        PrefixManager.initializeNames(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Core.getInstance().getPlayerManager().load(uuid).subscribe();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PermissionPlayer permissionPlayer = (PermissionPlayer) Core.getInstance().getPlayerManager().get(player.getUniqueId());
        permissionPlayer.save();

        PermissionInjection.uninject(player);

        PrefixManager.removeCache(player);
    }
}
