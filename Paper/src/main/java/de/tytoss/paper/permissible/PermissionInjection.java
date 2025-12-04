package de.tytoss.paper.permissible;

import de.tytoss.core.entity.base.PermissionOwner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;

public class PermissionInjection {
    private static Field PERMISSIBLE_FIELD;

    public static void inject(Player player, PermissionOwner owner) {
        try {
            Class<?> craftPlayerClass = player.getClass();
            Class<?> craftHumanEntityClass = craftPlayerClass.getSuperclass();
            PERMISSIBLE_FIELD = craftHumanEntityClass.getDeclaredField("perm");
            PERMISSIBLE_FIELD.setAccessible(true);
            LegendPermissible permissible = new LegendPermissible(player, owner);
            PERMISSIBLE_FIELD.set(player, permissible);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uninject(Player player) {
        try {
            PERMISSIBLE_FIELD.set(player, new PermissibleBase(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
