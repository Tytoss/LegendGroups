package de.tytoss.core.metadata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MetaContainer {

    private final List<MetaData<?>> metaNodes = new ArrayList<>();

    public <T> void addMeta(MetaData<T> meta) {
        metaNodes.add(meta);
    }

    public void removeMeta(String key) {
        metaNodes.removeIf(meta -> meta.getKey().equals(key));
    }

    @SuppressWarnings("unchecked")
    public <T> List<MetaData<T>> getMeta(String key) {
        return metaNodes.stream()
                .filter(meta -> meta.getKey().equals(key) && !meta.isExpired())
                .map(meta -> (MetaData<T>) meta)
                .collect(Collectors.toList());
    }

    public <T> MetaData<T> getHighestPriorityMeta(String key) {
        List<MetaData<T>> metas = getMeta(key);
        return metas.stream()
                .max(Comparator.comparingInt(MetaData::getPriority))
                .orElse(null);
    }

    public <T> void setMeta(String key, T value) {
        setMeta(key, value, 0, null);
    }

    public <T> void setMeta(String key, T value, int priority, Long durationMillis) {
        Long expiry = null;
        if (durationMillis != null) {
            expiry = System.currentTimeMillis() + durationMillis;
        }
        removeMeta(key);
        addMeta(new MetaData<>(key, value, priority, expiry));
    }

    public List<MetaData<?>> getAll() {
        return new ArrayList<>(metaNodes);
    }

    public void cleanupExpired() {
        metaNodes.removeIf(MetaData::isExpired);
    }
}

