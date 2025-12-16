package de.tytoss.paper.commands;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PlayerMenuUtility;
import de.tytoss.paper.menu.menus.MainMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class LegendGroupCommand implements CommandExecutor {
    private final String prefix = LegendGroups.configManager.get().node("message", "prefix").getString();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) return false;

        if (args.length == 0) {
            String line_1 = LegendGroups.configManager.get().node("message", "rankCommand", "line_1").getString();
            String line_2 =  LegendGroups.configManager.get().node("message", "rankCommand", "line_2").getString();

            PermissionPlayer owner = (PermissionPlayer) Core.getInstance().getPlayerManager().get(player.getUniqueId());

            Set<PermissionGroup> groups = owner.getGroups();

            player.sendMessage(Component.text(prefix + line_1));
            for (PermissionGroup group : groups) {
                Long duration;
                MetaData<?> meta = owner.getMetaData().getFirstMeta(MetaKeys.TEMP_GROUP + group.getId());

                if (meta == null) {
                    duration = 0L;
                } else {
                    duration = meta.getExpiry();
                }

                String name;

                if (owner.getPrefix() != null) {
                    name = group.getPrefix();
                } else {
                    name = group.getName();
                }

                if (duration == 0L) {
                    player.sendMessage(Component.text(prefix + line_2.replace("%rank%", "§a§l" + name + "§7").replace("%duration%", "§c§lPERMANENT")));
                } else {
                    player.sendMessage(Component.text(prefix + line_2.replace("%rank%", "§a§l" + name + "§7").replace("%duration%",
                            Instant.ofEpochMilli(duration)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))));
                }
            }
            return false;
        }

        if (args.length != 1 && !args[0].equals("editor")) {
            String commandUsage = LegendGroups.configManager.get().node("message", "error", "commandUsage").getString();

            player.sendMessage(Component.text(prefix + commandUsage));
            return false;
        }

        if (player.hasPermission("lg.editor")) {
            PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
            MainMenu mainMenu = new MainMenu(playerMenuUtility, null);
            mainMenu.open();
            return true;
        }

        return false;
    }
}
