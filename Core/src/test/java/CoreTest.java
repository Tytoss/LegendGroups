import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.manager.GroupManager;
import de.tytoss.core.manager.PlayerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@Testcontainers
class CoreTest {

    private Core core;
    private PlayerManager playerManager;
    private GroupManager groupManager;

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
        playerManager = core.getPlayerManager();
        groupManager = core.getGroupManager();
    }

    @Test
    void testPlayerManagerInitialization() {
        assert playerManager != null;
    }

    @Test
    void testGroupManagerInitialization() {
        assert groupManager != null;
    }

    @Test
    void testCreatePlayer() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = (PermissionPlayer) playerManager.create(uuid, "TestPlayer");

        assert player != null;
        assert player.getName().equals("TestPlayer");
        assert player.getId().equals(uuid);
        assert player.getGroups().contains(groupManager.getDefaultGroup());
        assert playerManager.get(uuid) != null;
        assert playerManager.get(uuid).equals(player);
    }

    @Test
    void testDuplicatePlayerCreation() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = (PermissionPlayer) playerManager.create(uuid, "TestPlayer");
        PermissionPlayer player2 = (PermissionPlayer) playerManager.create(uuid, "TestPlayer2");

        assert player.equals(player2);
        assert player2.getName().equals("TestPlayer");
    }
}
