import de.tytoss.core.Core;
import de.tytoss.core.database.DatabaseManager;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.manager.GroupManager;
import de.tytoss.core.manager.PlayerManager;
import de.tytoss.core.metadata.keys.MetaKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@Testcontainers
class PermissionTest {

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
    void testPermissionAddAndHas() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid, "TestPlayer");

        player.addPermission("test", Boolean.TRUE);

        assert player.hasPermission("test");
        assert !player.hasPermission("test2");
    }

    @Test
    void testPermissionRemove() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid, "TestPlayer");

        player.addPermission("test", Boolean.TRUE);
        assert player.hasPermission("test");

        player.removePermission("test");
        assert !player.hasPermission("test");
    }

    @Test
    void testStarPermission() {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid, "TestPlayer");

        player.addPermission("*", Boolean.TRUE);
        assert player.hasPermission("test");
        assert player.hasPermission("test2");
    }

    @Test
    void testGroupPermission() {
        UUID uuid1 = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid1, "TestPlayer1");

        UUID uuid2 = UUID.randomUUID();
        PermissionGroup group = new PermissionGroup(uuid2, "TestGroup");
        groupManager.save(group);

        group.addPermission("test", Boolean.TRUE);
        player.addGroup(group);

        assert player.hasPermission("test");
    }

    @Test
    void testGroupPermissionInheritance() {
        UUID uuid1 = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid1, "TestPlayer1");

        UUID uuid2 = UUID.randomUUID();
        PermissionGroup group = new PermissionGroup(uuid2, "TestGroup");
        groupManager.save(group);

        UUID uuid3 = UUID.randomUUID();
        PermissionGroup group2 = new PermissionGroup(uuid3, "TestGroup2");
        groupManager.save(group2);

        group2.addPermission("test", Boolean.TRUE);
        group.addInheritedGroup(group2);

        player.addGroup(group);

        assert player.hasPermission("test");
    }

    @Test
    void testGroupRemovePermission() {
        UUID uuid1 = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid1, "TestPlayer1");

        UUID uuid2 = UUID.randomUUID();
        PermissionGroup group = new PermissionGroup(uuid2, "TestGroup");
        groupManager.save(group);

        group.addPermission("test", Boolean.TRUE);
        player.addGroup(group);

        assert player.hasPermission("test");

        player.removeGroup(group);
        assert !player.hasPermission("test");
    }

    @Test
    void testPermissionExpired() throws InterruptedException {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid, "TestPlayer");

        player.addPermission("test", Boolean.TRUE, "1s");

        assert player.hasPermission("test");

        Thread.sleep(1100L);

        assert !player.hasPermission("test");
    }

    @Test
    void testTemporaryGroup() throws InterruptedException {
        UUID uuid = UUID.randomUUID();
        PermissionPlayer player = new PermissionPlayer(uuid, "TestPlayer");

        UUID uuid2 = UUID.randomUUID();
        PermissionGroup group = new PermissionGroup(uuid2, "TestGroup");
        groupManager.save(group);
        group.addPermission("test", Boolean.TRUE);

        player.addGroup(group, "1s");

        assert player.hasPermission("test");

        Thread.sleep(1100L);

        assert !player.hasPermission("test");
    }
}