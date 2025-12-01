package de.tytoss.core.entity.base;


import de.tytoss.core.Core;
import de.tytoss.core.metadata.MetaContainer;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.core.entity.types.PermissionOwnerType;
import de.tytoss.core.utils.DurationParser;

import java.util.UUID;

public abstract class PermissionOwner {

    protected final UUID id;
    protected String name;
    protected MetaContainer metaData = new MetaContainer();
    private final PermissionOwnerType type;

    public PermissionOwner(UUID id, String name, PermissionOwnerType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

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

    public void setDisplayName(String newName) {
        this.name = newName;
    }

    public <T> T getMeta(MetaData<T> key) {
        MetaData<T> meta = metaData.getFirstMeta(key.getKey());
        return meta != null ? meta.getValue() : null;
    }

    public <T> void setMeta(MetaData<T> key, T value) {
        metaData.setMeta(key.getKey(), value);
    }

    public void removeMeta(MetaData<?> key) {
        metaData.removeMeta(key.getKey());
    }

    public void cleanMeta() {
        metaData.getAll().forEach(meta -> metaData.removeMeta(meta.getKey()));
    }

    public void addPermission(String permission, boolean value, String duration) {
        long durationMillis = DurationParser.parseDuration(duration);
        Long expiry = System.currentTimeMillis() + durationMillis;

        metaData.addMeta(new MetaData<>(MetaKeys.PERMISSIONS + permission, value, expiry));
    }

    public void addPermission(String permission, boolean value) {
        Long expiry = null;
        metaData.addMeta(new MetaData<>(MetaKeys.PERMISSIONS + permission, value, expiry));
    }

    public void addPermission(String permission) {
        Boolean value = true;
        Long expiry = null;
        metaData.addMeta(new MetaData<>(MetaKeys.PERMISSIONS + permission, value, expiry));
    }

    public boolean hasPermission(String permission) {
        metaData.cleanupExpired();

        if (metaData.getFirstMeta(MetaKeys.PERMISSIONS + "*") != null && Boolean.TRUE.equals(metaData.getFirstMeta(MetaKeys.PERMISSIONS + "*").getValue())) {
            return true;
        }

        MetaData<Boolean> node = metaData.getFirstMeta(
                MetaKeys.PERMISSIONS + permission
        );

        return node != null && Boolean.TRUE.equals(node.getValue());
    }

    public void removePermission(String permission) {
        metaData.removeMeta(MetaKeys.PERMISSIONS + permission);
    }

    public void save() {
        switch (type) {
            case PLAYER -> Core.getInstance().getPlayerManager().save(this);
            case GROUP -> Core.getInstance().getGroupManager().save(this);
        }
    }

    public void replaceMetaContainer(MetaContainer metaContainer) {
        metaData = metaContainer;
    }
}

