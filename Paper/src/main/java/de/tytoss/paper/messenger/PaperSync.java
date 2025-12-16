package de.tytoss.paper.messenger;

import de.tytoss.core.Core;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.synchronizing.MetaSerializer;
import de.tytoss.paper.LegendGroups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PaperSync {

    public static void syncScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (PermissionOwner permissionOwner : Core.getInstance().getPlayerManager().getAll()) {
                    permissionOwner.save();
                    Player p = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
                    if (p != null) {
                        sendSync(p, permissionOwner);
                    }
                }
                for (PermissionOwner permissionOwner : Core.getInstance().getGroupManager().getAll()) {
                    permissionOwner.save();
                    Player p = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
                    if (p != null) {
                        sendSync(p, permissionOwner);
                    }
                }
            }
        }.runTaskTimerAsynchronously(LegendGroups.getInstance(), 0L, 6000L);
    }

    public static void sendSync(Player player, PermissionOwner permissionOwner) {

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeUTF(permissionOwner.getId().toString());
            dos.writeUTF(permissionOwner.getName());

            byte[] meta = MetaSerializer.serialize(permissionOwner.getMetaData());
            dos.write(meta);

            player.sendPluginMessage(LegendGroups.getInstance(), "legendgroups:sync", baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
