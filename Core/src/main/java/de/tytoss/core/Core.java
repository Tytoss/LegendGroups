package de.tytoss.core;

import de.tytoss.core.manager.GroupManager;
import de.tytoss.core.manager.PlayerManager;

public class Core {
    private static Core instance;
    private PlayerManager playerManager;
    private GroupManager groupManager;

    private Core() {}

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
            instance.init();
        }
        return instance;
    }
    public void init() {
        playerManager = new PlayerManager();
        groupManager = new GroupManager();
        instance = this;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }
}
