package de.tytoss.core.database;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import reactor.core.publisher.Mono;

public class DatabaseManager {

    private static ConnectionFactory connectionFactory;

    public void init(String host, int port, String database, String user, String password) {
        var options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                .option(ConnectionFactoryOptions.PROTOCOL, "postgresql")
                .option(ConnectionFactoryOptions.HOST, host)
                .option(ConnectionFactoryOptions.PORT, port)
                .option(ConnectionFactoryOptions.DATABASE, database)
                .option(ConnectionFactoryOptions.USER, user)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .build();

        connectionFactory = ConnectionFactories.get(options);
        createTables().block();
    }

    public static Mono<Connection> getConnection() {
        return Mono.from(connectionFactory.create());
    }

    private Mono<Void> createTables() {
        String createOwners = """
                CREATE TABLE IF NOT EXISTS permission_owners (
                    id UUID PRIMARY KEY,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL
                );
                """;

        String createMeta = """
                CREATE TABLE IF NOT EXISTS meta_data (
                    owner_id UUID REFERENCES permission_owners(id) ON DELETE CASCADE,
                    key TEXT NOT NULL,
                    value TEXT NOT NULL,
                    type TEXT NOT NULL,
                    expiry BIGINT,
                    PRIMARY KEY(owner_id, key)
                );
                """;

        return Mono.usingWhen(
                getConnection(),
                conn -> Mono.from(conn.createStatement(createOwners).execute())
                        .then(Mono.from(conn.createStatement(createMeta).execute()))
                        .then(),
                conn -> Mono.from(conn.close())
        );
    }
}
