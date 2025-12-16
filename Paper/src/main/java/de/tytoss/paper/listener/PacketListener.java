package de.tytoss.paper.listener;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;

public class PacketListener implements com.github.retrooper.packetevents.event.PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {

        User user = event.getUser();

        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {

            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event);

            WrapperPlayServerPlayerInfo.Action action = packet.getAction();

            if (action == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) {
                packet.getPlayerDataList().stream().findFirst().ifPresent(playerData -> {
                    if (user.getProfile() == playerData.getUserProfile()) {
                        System.out.println("Test");
                    }
                });
            }
        }
    }
}
