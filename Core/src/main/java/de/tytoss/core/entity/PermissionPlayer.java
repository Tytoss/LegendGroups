package de.tytoss.core.entity;

import de.tytoss.core.Core;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.entity.types.PermissionOwnerType;
import de.tytoss.core.utils.DurationParser;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionPlayer extends PermissionOwner {

    public PermissionPlayer(UUID id, String name) {
        super(id, name, PermissionOwnerType.PLAYER);
    }

    public Set<PermissionGroup> getGroups() {
        return metaData.getAll().stream()
            .filter(meta -> meta.getKey().startsWith(MetaKeys.GROUP) || meta.getKey().startsWith(MetaKeys.TEMP_GROUP))
            .filter(meta -> !meta.isExpired())
            .map(meta ->
                (PermissionGroup) Core.getInstance().getGroupManager().get(
                    UUID.fromString(meta.getValue().toString()
            )))
            .filter(group -> group != null)
            .collect(Collectors.toSet());
    }


    @Override
    public boolean hasPermission(String permission) {
        metaData.cleanupExpired();
        if (metaData.getFirstMeta(MetaKeys.PERMISSIONS + "*") != null && Boolean.TRUE.equals(metaData.getFirstMeta(MetaKeys.PERMISSIONS + "*").getValue())) {
            return true;
        }

        MetaData<Boolean> directNode = metaData.getFirstMeta(MetaKeys.PERMISSIONS + permission);

        if (directNode != null && Boolean.TRUE.equals(directNode.getValue())) {
            return true;
        }

        Iterator<PermissionGroup> it = getGroups().iterator();
        while (it.hasNext()) {
            PermissionGroup group = it.next();

            MetaData<UUID> tempGroupMeta = metaData.getFirstMeta(MetaKeys.TEMP_GROUP + group.getId());
            MetaData<UUID> permanentGroupMeta = metaData.getFirstMeta(MetaKeys.GROUP + group.getId());

            if (tempGroupMeta != null && tempGroupMeta.isExpired()) {
                it.remove();
                continue;
            }

            if (tempGroupMeta == null && permanentGroupMeta == null) {
                it.remove();
                continue;
            }

            if (group.hasPermission(permission)) {
                return true;
            }
        }

        for (PermissionGroup group : getGroups()) {
            if (group.getInheritedGroups().stream().anyMatch(inherited -> inherited.hasPermission(permission))) {
                return true;
            }
        }

        return false;
    }

    public void addGroup(PermissionGroup group) {
        if (getGroups().contains(group)) return;
        metaData.addMeta(new MetaData<>(MetaKeys.GROUP + group.getId(), group.getId(), null));
    }

    public void addGroup(PermissionGroup group, String duration) {
        if (getGroups().contains(group)) return;
        long durationMillis = DurationParser.parseDuration(duration);
        metaData.addMeta(new MetaData<>(MetaKeys.TEMP_GROUP + group.getId(), group.getId(), System.currentTimeMillis() + durationMillis));
    }

    public void removeGroup(PermissionGroup group) {
        if (!getGroups().contains(group)) return;
        if (metaData.getFirstMeta(MetaKeys.GROUP + group.getId()) != null) {
            metaData.removeMeta(MetaKeys.GROUP + group.getId());
        }
        if(metaData.getFirstMeta(MetaKeys.TEMP_GROUP + group.getId()) != null) {
            metaData.removeMeta(MetaKeys.TEMP_GROUP + group.getId());
        }
    }
}
