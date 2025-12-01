package de.tytoss.velocity.listener;


import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.proxy.Player;
import de.tytoss.core.Core;

import java.util.UUID;

public class PlayerJoinListener {

    @Subscribe
    public void onJoin(PostLoginEvent event) {

        Player player = event.getPlayer();

        try {
            Core.getInstance().getPlayerManager().get(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onPreJoin(PreLoginEvent event) {

        UUID uuid = event.getUniqueId();

        System.out.println("PreJoin: " + uuid);

        Core.getInstance().getPlayerManager().load(uuid).subscribe();
    }
}
