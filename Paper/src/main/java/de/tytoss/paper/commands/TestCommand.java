package de.tytoss.paper.commands;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.paper.messenger.PaperSync;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;


        Core.getInstance().getPlayerManager().load(player.getUniqueId()).subscribe( permissionPlayer -> {
            PermissionPlayer permPlayer = (PermissionPlayer) permissionPlayer;

            PermissionGroup group = (PermissionGroup) Core.getInstance().getGroupManager().create(UUID.randomUUID(), "Test");
            permPlayer.addGroup(group);
            group.addPermission("test");

            System.out.println(permissionPlayer.hasPermission("test"));

            PaperSync.sendSync(player, group);
            PaperSync.sendSync(player, permissionPlayer);
        });
        return true;
    }
}
