package de.tytoss.core.metadata;

public class MetaData<T> {

    private final String key;
    private final T value;
    private final Long expiry;

    public MetaData(String key, T value) {
        this(key, value, null);
    }

    public MetaData(String key, T value, Long expiry) {
        this.key = key;
        this.value = value;
        this.expiry = expiry;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public Long getExpiry() {
        return expiry;
    }

    public boolean isExpired() {
        if (expiry == null) return false;
        return System.currentTimeMillis() > expiry;
    }

    public Long getRemainingTime() {
        if (expiry == null) return null;
        return expiry - System.currentTimeMillis();
    }
}

