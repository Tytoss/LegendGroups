package de.tytoss.core.database;

import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.entity.types.PermissionOwnerType;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.MetaParser;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionOwnerRepository {

    public static @NotNull Mono<PermissionOwner> load(UUID uuid) {
        return DatabaseManager.getConnection()
            .flatMapMany(conn -> {
                String ownerSql = "SELECT type, name FROM permission_owners WHERE id = $1";

                return Flux.from(conn.createStatement(ownerSql)
                    .bind("$1", uuid)
                    .execute())
                    .flatMap(result -> result.map((row, meta) -> {
                        String type = row.get("type", String.class);
                        String name = row.get("name", String.class);

                        if ("PLAYER".equals(type)) {
                            return new PermissionPlayer(uuid, name);
                        } else if ("GROUP".equals(type)) {
                            return new PermissionGroup(uuid, name);
                        } else {
                            throw new IllegalArgumentException("Unknown PermissionOwner type: " + type);
                        }
                        }))
                        .doFinally(signal -> conn.close());
                })
                .next()
                .flatMap(owner -> DatabaseManager.getConnection()
                    .flatMapMany(conn -> {
                        String metaSql = "SELECT key, value, type, expiry FROM meta_data WHERE owner_id = $1";

                        return Flux.from(conn.createStatement(metaSql)
                            .bind("$1", uuid)
                            .execute())
                            .flatMap(result -> result.map((row, rowMeta) -> {
                                String key = row.get("key", String.class);
                                String value = row.get("value", String.class);
                                String metaType = row.get("type", String.class);
                                Long expiry = row.get("expiry", Long.class);

                                Object parsed = MetaParser.parseMetaValue(value, metaType);
                                return new MetaData<>(key, parsed, expiry);
                            }))
                            .doFinally(signal -> conn.close());
                    })
                    .collectList()
                    .map(metas -> {
                        metas.forEach(owner.getMetaData()::addMeta);
                        return owner;
                    })
                );
    }


    public static @NotNull Mono<List<PermissionGroup>> loadGroups() {
        return DatabaseManager.getConnection()
            .flatMapMany(conn -> {
                String sql = "SELECT id, name FROM permission_owners WHERE type = $1";
                return Flux.from(conn.createStatement(sql)
                        .bind("$1", "GROUP")
                        .execute())
                    .flatMap(result -> result.map((row,meta) -> {
                        UUID id = row.get("id", UUID.class);
                        String name = row.get("name", String.class);
                        return new PermissionGroup(id, name);
                    }))
                    .doFinally(signal -> conn.close());
            })
            .flatMap(group ->
                DatabaseManager.getConnection()
                    .flatMapMany(conn -> {
                        String metaSql = "SELECT key, value, type, expiry FROM meta_data WHERE owner_id = $1";
                        return Flux.from(conn.createStatement(metaSql)
                                .bind("$1", group.getId())
                                .execute())
                            .flatMap(result -> result.map((row, meta) -> {
                                String key = row.get("key", String.class);
                                String value = row.get("value", String.class);
                                String type = row.get("type", String.class);
                                Long expiry = row.get("expiry", Long.class);

                                Object parsedValue = MetaParser.parseMetaValue(value, type);
                                return new MetaData<>(key, parsedValue, expiry);
                            }))
                            .collectList()
                            .map(metas -> {
                                metas.forEach(group.getMetaData()::addMeta);
                                return group;
                            })
                            .doFinally(signal -> conn.close());
                    })
            )
            .collectList();
    }

    public static @NotNull Mono<Void> save(PermissionOwner owner) {
        return Mono.usingWhen(
                DatabaseManager.getConnection(),
                conn -> {
                    String insertOwner = """
            INSERT INTO permission_owners(id, name, type)
            VALUES ($1, $2, $3)
            ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name
            """;

                    String typeString;
                    if (owner instanceof PermissionPlayer) typeString = "PLAYER";
                    else if (owner instanceof PermissionGroup) typeString = "GROUP";
                    else return Mono.error(new IllegalArgumentException("Unknown PermissionOwner type"));

                    Mono<Void> saveOwner = Mono.from(conn.createStatement(insertOwner)
                                    .bind("$1", owner.getId())
                                    .bind("$2", owner.getName())
                                    .bind("$3", typeString)
                                    .execute())
                            .then();

                    Mono<List<String>> existingKeys = Mono.from(conn.createStatement(
                                            "SELECT key FROM meta_data WHERE owner_id = $1")
                                    .bind("$1", owner.getId())
                                    .execute())
                            .flatMapMany(result -> result.map((row, meta) -> row.get("key", String.class)))
                            .collectList();

                    Flux<Void> upsertMetas = Flux.fromIterable(owner.getMetaData().getAll())
                            .flatMap(meta -> {
                                String upsertMeta = """
                        INSERT INTO meta_data(owner_id, key, value, type, expiry)
                        VALUES ($1, $2, $3, $4, $5)
                        ON CONFLICT (owner_id, key)
                        DO UPDATE SET value = EXCLUDED.value, type = EXCLUDED.type, expiry = EXCLUDED.expiry
                        """;
                                var stmt = conn.createStatement(upsertMeta)
                                        .bind("$1", owner.getId())
                                        .bind("$2", meta.getKey())
                                        .bind("$3", meta.getValue().toString())
                                        .bind("$4", meta.getValue().getClass().getSimpleName());

                                if (meta.getExpiry() != null)
                                    stmt.bind("$5", meta.getExpiry());
                                else
                                    stmt.bindNull("$5", Long.class);

                                return Mono.from(stmt.execute()).then();
                            });

                    Mono<Void> deleteOldMetas = existingKeys.flatMapMany(keysInDb -> {
                        Set<String> newKeys = owner.getMetaData().getAll().stream()
                                .map(m -> m.getKey())
                                .collect(Collectors.toSet());

                        return Flux.fromIterable(keysInDb)
                                .filter(key -> !newKeys.contains(key))
                                .flatMap(key ->
                                        Mono.from(conn.createStatement("DELETE FROM meta_data WHERE owner_id = $1 AND key = $2")
                                                        .bind("$1", owner.getId())
                                                        .bind("$2", key)
                                                        .execute())
                                                .then()
                                );
                    }).then();

                    return saveOwner.thenMany(upsertMetas).then(deleteOldMetas).then();
                },
                conn -> conn.close()
        );

    }


    public static @NotNull Mono<PermissionOwner> create(UUID uuid, String name, PermissionOwnerType type) {
        String sql = "INSERT INTO permission_owners(id, name, type) VALUES ($1, $2, $3)";

        return DatabaseManager.getConnection()
            .flatMap(conn ->
                Mono.from(conn.createStatement(sql)
                        .bind("$1", uuid)
                        .bind("$2", name)
                        .bind("$3", type.name())
                        .execute())
                    .flatMap(res -> Mono.from(res.getRowsUpdated()))
                    .then(Mono.fromCallable(() -> {
                        if (type == PermissionOwnerType.PLAYER) return new PermissionPlayer(uuid, name);
                        else return new PermissionGroup(uuid, name);
                    }))
                    .doFinally(signal -> conn.close())
            );
    }

    public static @NotNull Mono<Void> delete(UUID uuid) {
        String sql = "DELETE FROM permission_owners WHERE id = $1";
        String metaSql = "DELETE FROM meta_data WHERE id = $1";

        return Mono.usingWhen(
                DatabaseManager.getConnection(),
                conn -> Mono.from(conn.createStatement(sql).bind("$1", uuid).execute())
                        .then(Mono.from(conn.createStatement(metaSql).bind("$1", uuid).execute()))
                        .then(),
                conn -> Mono.from(conn.close())
        );
    }
}

