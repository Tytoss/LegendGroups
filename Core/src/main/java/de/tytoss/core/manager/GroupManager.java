package de.tytoss.core.manager;

import de.tytoss.core.manager.base.OwnerManager;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.base.PermissionOwner;

import java.util.UUID;

public class GroupManager extends OwnerManager {

    private final PermissionGroup defaultGroup;

    public GroupManager() {
        super();
        defaultGroup = new PermissionGroup(UUID.randomUUID(), "default");
        save(defaultGroup);
    }

    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }
    @Override
    public PermissionOwner create(UUID uuid, String name) {
        if (get(uuid) != null) return get(uuid);
        PermissionOwner group = new PermissionGroup(uuid, name);
        save(group);
        cache(group);
        return group;
    }
}
