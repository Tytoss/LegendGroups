package de.tytoss.velocity.messenger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import de.tytoss.core.Core;
import de.tytoss.core.database.PermissionOwnerRepository;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.metadata.MetaContainer;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.synchronizing.MetaSerializer;
import de.tytoss.velocity.Velocity;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public class PluginMessageHandler {

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if(!Objects.equals(event.getIdentifier().getId(), "legendgroups:sync")) return;

        byte[] data = event.getData();

        Velocity.server.getScheduler()
                .buildTask(Velocity.getInstance(), () -> {
                    try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {

                        UUID target = UUID.fromString(dis.readUTF());
                        String targetName = dis.readUTF();

                        List<MetaData<?>> metaData = MetaSerializer.deserialize(dis.readAllBytes());

                        MetaContainer container = new MetaContainer();
                        for (MetaData<?> metaNode : metaData) {
                            container.addMeta(metaNode);
                        }

                        PermissionOwnerRepository.load(target)
                                .switchIfEmpty(Mono.fromRunnable(() -> {
                                    Optional<Player> player = Velocity.server.getPlayer(target);
                                    PermissionOwner permissionOwner;
                                    if (player.isPresent()) {
                                        permissionOwner = Core.getInstance().getPlayerManager().create(target, targetName);
                                    } else {
                                        permissionOwner = Core.getInstance().getGroupManager().create(target, targetName);
                                    }
                                    permissionOwner.replaceMetaContainer(container);
                                    }
                                ))
                                .doOnNext(owner -> {
                                    owner.replaceMetaContainer(container);
                                })
                                .doOnError(Throwable::printStackTrace)
                                .subscribe();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).schedule();
    }
}


