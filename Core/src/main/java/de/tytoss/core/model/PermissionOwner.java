package de.tytoss.core.model;


import de.tytoss.core.metadata.MetaContainer;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.core.model.types.PermissionOwnerType;

import java.util.UUID;

public abstract class PermissionOwner {

    protected UUID id;
    protected String name;
    protected MetaContainer metaData;
    protected PermissionOwnerType type;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MetaContainer getMetaData() {
        return metaData;
    }

    public PermissionOwnerType getType() {
        return type;
    }

    public String getDisplayName() {
        return name;
    }

    public void setDisplayName(String newName) {
        this.name = newName;
    }

    public void addPermission(String permission, boolean value, Long durationMillis) {
        Long expiry = null;

        if (durationMillis != null) {
            expiry = System.currentTimeMillis() + durationMillis;
        }

        metaData.addMeta(new MetaData<>(MetaKeys.PERMISSIONS + permission, value, expiry));
    }

    public boolean hasPermission(String permission) {
        metaData.cleanupExpired();

        MetaData<Boolean> node = metaData.getHighestPriorityMeta(
                MetaKeys.PERMISSIONS + permission
        );

        return node != null && Boolean.TRUE.equals(node.getValue());
    }

    public void removePermission(String permission) {
        metaData.removeMeta(MetaKeys.PERMISSIONS + permission);
    }
}

