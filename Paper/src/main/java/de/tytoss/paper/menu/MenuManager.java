package de.tytoss.paper.menu;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    private static final Map<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private static boolean isSetup = false;

    private static void registerMenuListener(Server server) {
        boolean isAlreadyRegistered = false;

        for (var listener : InventoryClickEvent.getHandlerList().getRegisteredListeners()) {
            if (listener.getListener() instanceof MenuListener) {
                isAlreadyRegistered = true;
                break;
            }
        }

        if (!isAlreadyRegistered) {
            server.getPluginManager().registerEvents(
                    new MenuListener(),
                    server.getPluginManager().getPlugins()[0]
            );
        }
    }

    public static void setup(Server server) {
        registerMenuListener(server);
        isSetup = true;
    }

    public static void openMenu(Class<? extends Menu> menuClass, Player player) {
        try {
            var constructor = menuClass.getConstructor(PlayerMenuUtility.class);
            Menu menu = constructor.newInstance(getPlayerMenuUtility(player));
            menu.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        if (!isSetup) return null;

        return playerMenuUtilityMap.computeIfAbsent(player, PlayerMenuUtility::new);
    }
}
