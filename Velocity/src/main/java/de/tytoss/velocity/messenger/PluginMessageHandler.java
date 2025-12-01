package de.tytoss.velocity.messenger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.core.metadata.MetaContainer;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.synchronizing.MetaSerializer;
import de.tytoss.velocity.Velocity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PluginMessageHandler {

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if(!event.getIdentifier().equals("legendgroups:sync")) return;

        byte[] data = event.getData();

        Velocity.server.getScheduler()
                .buildTask(Velocity.getInstance(), () -> {
                    try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {

                        UUID target = UUID.fromString(dis.readUTF());

                        int size = dis.readInt();

                        byte[] meta = new byte[size];
                        dis.readFully(meta);

                        List<MetaData<?>> metaData = MetaSerializer.deserialize(meta);

                        MetaContainer container = new MetaContainer();

                        PermissionOwnerRepository.load(target).subscribe(owner -> {
                            if (owner == null) return;

                            for (MetaData<?> metaNode : metaData) {
                                container.addMeta(metaNode);
                            }

                            owner.replaceMetaContainer(container);
                            owner.save();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).schedule();
    }
}


