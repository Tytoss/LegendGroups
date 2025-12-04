package de.tytoss.core.metadata;

import java.util.UUID;

public final class MetaParser {

    private MetaParser() {}

    public static Object parseMetaValue(String value, String type) {
        if (type == null) return value;

        switch (type.toUpperCase()) {
            case "STRING": return value;
            case "INT":
            case "INTEGER":
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return 0;
                }
            case "LONG":
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            case "BOOLEAN": return "true".equalsIgnoreCase(value);
            case "DOUBLE":
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            case "FLOAT":
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    return 0f;
                }
            case "UUID": return UUID.fromString(value);
            default: return value;
        }
    }
}

