package de.tytoss.paper.prefix;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import de.tytoss.core.entity.PermissionPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PrefixManager {
    public static String resolvePrefix(PermissionPlayer player) {
        String display = player.getPrefix();
        if (display != null && !display.isBlank()) return display;

        String groupPrefix = player.getPrimaryGroup().getPrefix();
        if (groupPrefix != null && !groupPrefix.isBlank()) return groupPrefix;

        return "";
    }

    private static final Map<UUID, String> prefixCache = new HashMap<>();
    private static final Map<UUID, Integer> playerDisplayCache = new HashMap<>();
    private static final Map<Integer, UUID> entityUUIDCache = new HashMap<>();

    private static Team team;

    public static void update(PermissionPlayer player) {
        String resolved = resolvePrefix(player);
        String old = prefixCache.get(player.getId());

        System.out.println(resolved);
        System.out.println(old);

        if (old == null || !old.equals(resolved)) {
            prefixCache.put(player.getId(), resolved);
            updatePlayerDisplayName(player);
        }
    }

    public static void updatePlayerDisplayName(PermissionPlayer target) {
        StringBuilder prefix = new StringBuilder(resolvePrefix(target));

        Player player = Bukkit.getPlayer(target.getId());

        target.getPrimaryGroup().getWeight();

        int inverted = 0xFFF - Math.min(target.getPrimaryGroup().getWeight(), 0xFFF);
        String hex = String.format("%03x", inverted);

        StringBuilder out = new StringBuilder();
        for (char c : hex.toCharArray()) {
            out.append('§').append(c);
        }

        prefix.insert(0, out);

        Component displayName = Component.text(prefix.toString().replace("&", "§") + "§r" +  target.getName());

        if (player == null) return;

        player.displayName(displayName);
        player.playerListName(displayName);

        Integer entityId;
        Entity entity;

        if (playerDisplayCache.containsKey(target.getId())) {
            entityId = playerDisplayCache.get(target.getId());
            UUID entityUUID = entityUUIDCache.get(entityId);
            entity = Bukkit.getEntity(entityUUID);
        } else {
            entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.TEXT_DISPLAY);
            entityId = entity.getEntityId();
            entityUUIDCache.put(entityId, entity.getUniqueId());
        }

        PacketWrapper<@NotNull WrapperPlayServerEntityMetadata> metaDataPacket = new WrapperPlayServerEntityMetadata(
                entityId,
                List.of(
                        new EntityData<>(11, EntityDataTypes.VECTOR3F, new Vector3f(0.0f, 0.3f, 0.0f)),
                        new EntityData<>(15, EntityDataTypes.BYTE, Integer.valueOf(3).byteValue()),
                        new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, displayName)
                )
        );

        player.addPassenger(entity);

        playerDisplayCache.put(player.getUniqueId(), entity.getEntityId());

        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, metaDataPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeCache(Player player) {
        prefixCache.remove(player.getUniqueId());
        playerDisplayCache.remove(player.getUniqueId());
    }

    public static void initializeNames(Player player) {
        if (team == null) {
            if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("LegendGroups") == null) {
                team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("LegendGroups");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                team.addPlayer(player);
            } else {
                team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("LegendGroups");
            }
        } else {
            team.addPlayer(player);
        }

        playerDisplayCache.forEach((uuid, entityId) -> {
            String prefix = prefixCache.get(uuid);
            Player p =  Bukkit.getPlayer(uuid);

            if (p == null) return;
            Component displayName = Component.text(prefix.replace("&", "§") + p.getName());

            PacketWrapper<@NotNull WrapperPlayServerEntityMetadata> metaDataPacket = new WrapperPlayServerEntityMetadata(
                    entityId,
                    List.of(
                            new EntityData<>(11, EntityDataTypes.VECTOR3F, new Vector3f(0.0f, 0.2f, 0.0f)),
                            new EntityData<>(15, EntityDataTypes.BYTE, Integer.valueOf(3).byteValue()),
                            new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, displayName)
                    )
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, metaDataPacket);
        });
    }
}
