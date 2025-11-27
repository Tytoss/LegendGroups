package de.tytoss.core.manager.base;

import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.core.entity.base.PermissionOwner;

import java.util.*;

public abstract class OwnerManager {
    private final Map<UUID, PermissionOwner> cache = new HashMap<>();

    public List<PermissionOwner> getAll() {
        return cache.values().stream().toList();
    }

    public PermissionOwner get(UUID id) {
        return cache.get(id);
    }

    public PermissionOwner load(UUID uuid) {
        if(get(uuid) == null) {
            PermissionOwner owner = PermissionOwnerRepository.load(uuid).block();
            cache.put(uuid, owner);
            return owner;
        } else return get(uuid);
    }

    public void save(PermissionOwner owner) {
        if (!cache.containsKey(owner.getId())) cache(owner);
        PermissionOwnerRepository.save(owner).subscribe();
    }

    public void cache(PermissionOwner owner) {
        if (cache.containsKey(owner.getId())) return;
        cache.put(owner.getId(), owner);
    }

    public abstract PermissionOwner create(UUID uuid, String name);
}
