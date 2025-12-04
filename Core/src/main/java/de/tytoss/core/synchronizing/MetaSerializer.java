package de.tytoss.core.synchronizing;

import de.tytoss.core.metadata.MetaContainer;
import de.tytoss.core.metadata.MetaData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MetaSerializer {

    public static byte[] serialize(MetaContainer container) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {

            dos.writeInt(container.getAll().size());

            for (MetaData<?> metaNode : container.getAll()) {
                dos.writeUTF(metaNode.getKey());

                Object metaValue = metaNode.getValue();

                switch (metaValue) {
                    case String s -> {
                        dos.writeUTF("STRING");
                        dos.writeUTF(s);
                    }
                    case Integer i -> {
                        dos.writeUTF("INT");
                        dos.writeInt(i);
                    }
                    case Long l -> {
                        dos.writeUTF("LONG");
                        dos.writeLong(l);
                    }
                    case Boolean b -> {
                        dos.writeUTF("BOOLEAN");
                        dos.writeBoolean(b);
                    }
                    case Double d -> {
                        dos.writeUTF("DOUBLE");
                        dos.writeDouble(d);
                    }
                    case Float f -> {
                        dos.writeUTF("FLOAT");
                        dos.writeFloat(f);
                    }
                    case UUID u -> {
                        dos.writeUTF("UUID");
                        dos.writeUTF(u.toString());
                    }
                    default ->
                            throw new IOException("Meta value type not supported: " + metaValue.getClass().getSimpleName());
                }

                dos.writeLong(metaNode.getExpiry() == null ? -1 : metaNode.getExpiry());
            }

            return bos.toByteArray();
        }
    }

    public static List<MetaData<?>> deserialize(byte[] data) throws IOException {
        List<MetaData<?>> meta = new ArrayList<>();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis)) {

            int size = dis.readInt();

            for (int i = 0; i < size; i++) {
                String key = dis.readUTF();
                String type = dis.readUTF();
                Object value;

                switch (type) {
                    case "STRING" -> value = dis.readUTF();
                    case "INT" -> value = dis.readInt();
                    case "LONG" -> value = dis.readLong();
                    case "BOOLEAN" -> value = dis.readBoolean();
                    case "DOUBLE" -> value = dis.readDouble();
                    case "FLOAT" -> value = dis.readFloat();
                    case "UUID" -> value = UUID.fromString(dis.readUTF());
                    default -> throw new IOException("Unknown Meta value type: " + type);
                }

                long expiryRaw = dis.readLong();
                Long expiry = expiryRaw == -1 ? null : expiryRaw;

                meta.add(new MetaData<>(key, value, expiry));
            }
        }
        return meta;
    }
}
