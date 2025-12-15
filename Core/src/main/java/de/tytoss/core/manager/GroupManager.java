package de.tytoss.core.manager;

import de.tytoss.core.Core;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.core.manager.base.OwnerManager;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GroupManager extends OwnerManager {

    private PermissionGroup defaultGroup;

    public GroupManager() {
        super();
        PermissionOwnerRepository.loadGroups().subscribe(groups -> {
            Optional<PermissionGroup> group = groups.stream().filter(permissionGroup -> Objects.equals(permissionGroup.getName(), "default")).findFirst();
            defaultGroup = group.orElseGet(() -> (PermissionGroup) create(UUID.randomUUID(), "default"));
        });
    }

    public PermissionGroup getDefaultGroup() {
        if (defaultGroup != null) {
            return defaultGroup;
        } else  {
            PermissionGroup group = (PermissionGroup) create(UUID.randomUUID(), "default");
            defaultGroup = group;
            return group;
        }
    }

    @Override
    public PermissionOwner create(UUID uuid, String name) {
        if (get(uuid) != null) return get(uuid);
        PermissionOwner group = new PermissionGroup(uuid, name);
        group.getMetaData().addMeta(new MetaData<>(MetaKeys.WEIGHT, 0));
        cache(group);
        save(group);
        return group;
    }
}
