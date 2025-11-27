package de.tytoss.core.manager;

import de.tytoss.core.Core;
import de.tytoss.core.manager.base.OwnerManager;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.base.PermissionOwner;

import java.util.UUID;

public class PlayerManager extends OwnerManager {
    @Override
    public PermissionOwner create(UUID uuid, String name) {
        if (get(uuid) != null) return get(uuid);
        PermissionPlayer player = new PermissionPlayer(uuid, name);
        player.addGroup(Core.getInstance().getGroupManager().getDefaultGroup());
        cache(player);
        return player;
    }
}
