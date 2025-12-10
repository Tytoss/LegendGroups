import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.entity.PermissionPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@Testcontainers
class CoreTest {

    private Core core;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setUp() {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.init(
                postgres.getHost(),
                postgres.getMappedPort(5432),
                postgres.getDatabaseName(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        core = Core.getInstance();
    }

    @Test
    void testPlayerManagerInitialization() {
        assert core.getPlayerManager() != null;
    }

    @Test
    void testGroupManagerInitialization() {
        assert core.getGroupManager() != null;
        assert core.getGroupManager().getDefaultGroup() != null;
    }

    @Test
    void testCreatePlayer() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = (PermissionPlayer) Core.getInstance().getPlayerManager().create(uuid, "TestPlayer");

        assert player != null;
        assert player.getName().equals("TestPlayer");
        assert player.getId().equals(uuid);
        assert player.getGroups().contains(Core.getInstance().getGroupManager().getDefaultGroup());
        assert core.getPlayerManager().get(uuid) != null;
        assert core.getPlayerManager().get(uuid).equals(player);
    }

    @Test
    void testDuplicatePlayerCreation() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = (PermissionPlayer) Core.getInstance().getPlayerManager().create(uuid, "TestPlayer");
        PermissionPlayer player2 = (PermissionPlayer) Core.getInstance().getPlayerManager().create(uuid, "TestPlayer2");

        assert player.equals(player2);
        assert player2.getName().equals("TestPlayer");
    }
}
