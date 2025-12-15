package de.tytoss.core.entity;

import de.tytoss.core.Core;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.entity.types.PermissionOwnerType;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.core.utils.DurationParser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermissionGroup extends PermissionOwner {

    private final Set<PermissionGroup> inheritedGroups = new HashSet<>();

    public PermissionGroup(UUID id, String name) {
        super(id, name, PermissionOwnerType.GROUP);
    }

    @Override
    public boolean hasPermission(String permission) {
        metaData.cleanupExpired();

        if (metaData.getFirstMeta(MetaKeys.PERMISSIONS + "*") != null && Boolean.TRUE.equals(metaData.getFirstMeta(MetaKeys.PERMISSIONS + "*").getValue())) {
            return true;
        }

        MetaData<Boolean> node = metaData.getFirstMeta(
                MetaKeys.PERMISSIONS + permission
        );

        for (PermissionGroup group : inheritedGroups) {
            if (group.hasPermission(permission)) {
                return true;
            }
        }

        return node != null && Boolean.TRUE.equals(node.getValue());
    }

    public Set<PermissionGroup> getInheritedGroups() {
        return inheritedGroups;
    }

    public void addInheritedGroup(PermissionGroup group) {
        if (inheritedGroups.contains(group)) return;
        metaData.addMeta(new MetaData<>(MetaKeys.INHERITED + group.getId(), true, null));
        inheritedGroups.add(group);
    }

    public void addInheritedGroup(PermissionGroup group, String duration) {
        if (inheritedGroups.contains(group)) return;
        long durationMillis = DurationParser.parseDuration(duration);
        metaData.addMeta(new MetaData<>(MetaKeys.INHERITED + group.getId(), true, System.currentTimeMillis() + durationMillis));
        inheritedGroups.add(group);
    }

    public void removeInheritedGroup(PermissionGroup group) {
        inheritedGroups.remove(group);
    }

    public int getWeight() {
        Object weight = metaData.getFirstMeta(MetaKeys.WEIGHT).getValue();
        if (weight == null) return 0;

        try {
            return Integer.parseInt(weight.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
