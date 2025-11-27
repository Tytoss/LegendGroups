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
                .filter(metaNode -> metaNode.getKey().equals(key) && !metaNode.isExpired())
                .map(meta -> (MetaData<T>) meta)
                .collect(Collectors.toList());
    }

    public <T> MetaData<T> getFirstMeta(String key) {
        List<MetaData<T>> meta = getMeta(key);
        return meta.stream()
                .filter(metaNode -> !metaNode.isExpired())
                .findFirst()
                .orElse(null);
    }

    public <T> void setMeta(String key, T value) {
        setMeta(key, value, null);
    }

    public <T> void setMeta(String key, T value, Long durationMillis) {
        Long expiry = null;
        if (durationMillis != null) {
            expiry = System.currentTimeMillis() + durationMillis;
        }
        removeMeta(key);
        addMeta(new MetaData<>(key, value, expiry));
    }

    public List<MetaData<?>> getAll() {
        return metaNodes;
    }

    public void cleanupExpired() {
        metaNodes.removeIf(MetaData::isExpired);
    }
}

