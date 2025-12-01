package de.tytoss.velocity.provider;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import de.tytoss.core.entity.PermissionPlayer;

public class LegendPermissionFunction implements PermissionFunction {

    private final PermissionPlayer permissionPlayer;

    public LegendPermissionFunction(PermissionPlayer permissionPlayer) {
        this.permissionPlayer = permissionPlayer;
    }

    @Override
    public Tristate getPermissionValue(String permission) {

        if (permission == null) return Tristate.UNDEFINED;
        if (permissionPlayer.hasPermission(permission)) return Tristate.TRUE;
        else return Tristate.FALSE;
    }
}
