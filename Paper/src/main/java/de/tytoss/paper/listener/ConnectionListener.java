package de.tytoss.paper.listener;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.paper.permissible.PermissionInjection;
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
        });
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
    }
}
