package de.tytoss.paper.permissible;

import de.tytoss.core.entity.base.PermissionOwner;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegendPermissible extends PermissibleBase {

    private final Player player;
    private final PermissionOwner permissionOwner;

    public LegendPermissible(Player player, PermissionOwner permissionOwner) {
        super(player);
        this.player = player;
        this.permissionOwner = permissionOwner;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return permissionOwner.hasPermission(permission);
    }
}
