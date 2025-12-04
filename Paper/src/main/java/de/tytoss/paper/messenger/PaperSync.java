package de.tytoss.paper.messenger;

import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.synchronizing.MetaSerializer;
import de.tytoss.paper.LegendGroups;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PaperSync {

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
