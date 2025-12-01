package de.tytoss.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionPlayer;

public class PlayerDisconnectListener {

    @Subscribe
    public void onLeave(DisconnectEvent event) {
        Player player = event.getPlayer();

        PermissionPlayer permissionPlayer = (PermissionPlayer) Core.getInstance().getPlayerManager().get(player.getUniqueId());
        permissionPlayer.save();
    }
}
