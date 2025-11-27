import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.types.PermissionOwnerType;
import de.tytoss.core.manager.GroupManager;
import de.tytoss.core.manager.PlayerManager;
import io.r2dbc.spi.Connection;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;


@Testcontainers
class DatabaseTest {

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
    void testDatabaseInit() {
        Connection conn = DatabaseManager.getConnection().block();
        assert conn != null;
        conn.close();
    }

    @Test
    void testSaveAndLoad() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid, "TestPlayer");

        UUID uuid2 = UUID.randomUUID();
        PermissionGroup group = new PermissionGroup(uuid2, "TestGroup");
        groupManager.save(group);

        player.addGroup(group);
        player.addPermission("test", Boolean.TRUE);

        PermissionOwnerRepository.save(player).block();

        PermissionPlayer databasePlayer = (PermissionPlayer) PermissionOwnerRepository.load(uuid).block();

        assert player.getId().equals(databasePlayer.getId());
        assert player.getName().equals(databasePlayer.getName());
        assert databasePlayer.hasPermission("test");
        assert databasePlayer.getGroups().contains(group);
    }

    @Test
    void testLoadNonExisting() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer databasePlayer = (PermissionPlayer) PermissionOwnerRepository.load(uuid).block();
        assert databasePlayer == null;
    }

    @Test
    void testCreate() {
        UUID uuid = UUID.randomUUID();
        PermissionOwnerRepository.create(uuid, "TestPlayer", PermissionOwnerType.PLAYER).block();

        PermissionPlayer databasePlayer = (PermissionPlayer) PermissionOwnerRepository.load(uuid).block();
        assert databasePlayer != null;
    }

    @Test
    void testDelete() {
        UUID uuid = UUID.randomUUID();
        PermissionOwnerRepository.create(uuid, "TestPlayer", PermissionOwnerType.PLAYER).block();

        PermissionPlayer databasePlayer = (PermissionPlayer) PermissionOwnerRepository.load(uuid).block();
        assert databasePlayer != null;

        PermissionOwnerRepository.delete(uuid).block();

        PermissionPlayer deletedPlayer = (PermissionPlayer) PermissionOwnerRepository.load(uuid).block();
        assert deletedPlayer == null;
    }

    @Test
    void testLoadAllGroups() {
        UUID uuid1 = UUID.randomUUID();
        PermissionGroup group = (PermissionGroup) groupManager.create(uuid1, "TestGroup");

        UUID uuid2 = UUID.randomUUID();
        PermissionGroup group2 = (PermissionGroup) groupManager.create(uuid2, "TestGroup2");

        group.addPermission("test", Boolean.TRUE);
        group2.addPermission("test", Boolean.TRUE);

        List<PermissionGroup> groups = PermissionOwnerRepository.loadGroups().block();

        for (PermissionGroup g : groups) {
            assert g.hasPermission("test");
        }
    }
}
