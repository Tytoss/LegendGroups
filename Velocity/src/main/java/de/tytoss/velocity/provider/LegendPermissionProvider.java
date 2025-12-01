package de.tytoss.velocity.provider;

import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.Player;
import de.tytoss.core.entity.PermissionPlayer;

public class LegendPermissionProvider implements PermissionProvider {

    private final Player player;
    private final PermissionPlayer permissionPlayer;

    public LegendPermissionProvider(Player player, PermissionPlayer permissionPlayer) {
        this.player = player;
        this.permissionPlayer = permissionPlayer;
    }

    @Override
    public LegendPermissionFunction createFunction(PermissionSubject subject) {
        if(subject.equals(player)) {
            return new LegendPermissionFunction(permissionPlayer);
        } else throw new IllegalArgumentException("Subject is not a player!");
    }
}
