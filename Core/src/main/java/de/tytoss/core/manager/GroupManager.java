package de.tytoss.core.manager;

import de.tytoss.core.Core;
import de.tytoss.core.manager.base.OwnerManager;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.base.PermissionOwner;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GroupManager extends OwnerManager {

    private final PermissionGroup defaultGroup;

    public GroupManager() {
        super();
        Optional<PermissionOwner> group = getAll().stream().filter(permissionGroup -> Objects.equals(permissionGroup.getName(), "default")).findFirst();
        if(group.isEmpty()) {
            defaultGroup = new PermissionGroup(UUID.randomUUID(), "default");
            save(defaultGroup);
        } else {
            defaultGroup = (PermissionGroup) group.get();
        }
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
