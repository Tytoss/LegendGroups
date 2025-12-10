package de.tytoss.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.Player;
import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.velocity.provider.LegendPermissionProvider;

public class PermissionSetupListener {

    @Subscribe
    public void onPermissionSetup(PermissionsSetupEvent event) {
        PermissionSubject subject = event.getSubject();
        if (!(subject instanceof Player player)) return;

        PermissionPlayer permissionPlayer = (PermissionPlayer) Core.getInstance().getPlayerManager().get(player.getUniqueId());

        if (permissionPlayer == null) {
            permissionPlayer = (PermissionPlayer) Core.getInstance().getPlayerManager().create(player.getUniqueId(), player.getUsername());
            permissionPlayer.save();
        }

        event.setProvider(new LegendPermissionProvider(player, permissionPlayer));
    }
}