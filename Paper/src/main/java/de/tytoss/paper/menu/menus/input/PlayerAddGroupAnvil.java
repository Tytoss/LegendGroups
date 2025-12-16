package de.tytoss.paper.menu.menus.input;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.utils.DurationParser;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.messenger.PaperSync;
import de.tytoss.paper.prefix.PrefixManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerAddGroupAnvil implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView() instanceof AnvilView inv)) return;

        Player player = (Player) event.getWhoClicked();

        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

        String name = serializer.serialize(event.getView().title());

        Player target = Bukkit.getPlayer(name);

        if (target == null) return;

        PermissionPlayer owner = (PermissionPlayer) Core.getInstance().getPlayerManager().get(target.getUniqueId());

        if (inputNamePlayer.remove(player.getUniqueId()) != null && event.getRawSlot() == 2) {
            event.setCancelled(true);

            String groupName = inv.getRenameText();

            if (groupName == null) return;

            tempName.put(player.getUniqueId(), groupName);
            openDurationAnvil(player, name);
            return;
        } else {
            event.setCancelled(true);
        }

        if (inputDurationPlayer.remove(player.getUniqueId()) != null && event.getRawSlot() == 2) {
            event.setCancelled(true);

            String duration = inv.getRenameText();

            if (duration == null) return;

            if (duration.equalsIgnoreCase("p")) {
                String groupName = tempName.remove(player.getUniqueId());

                if (groupName == null) return;

                PermissionGroup addedGroup = (PermissionGroup) Core.getInstance().getGroupManager().getAll().stream().filter(group -> group.getName().equals(groupName)).findFirst().orElse(null);

                if (addedGroup == null) {
                    TextComponent prefix = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "prefix").getString()));
                    TextComponent message = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "error", "ownerNotFound").getString()));

                    player.sendMessage(prefix.append(message));
                    return;
                }

                owner.addGroup(addedGroup);

                PaperSync.sendSync(player, owner);
                PrefixManager.update(owner);
                tempFrom.get(player.getUniqueId()).open();
                tempFrom.remove(player.getUniqueId());
                return;
            }

            long parsedDuration = DurationParser.parseDuration(duration);

            if (parsedDuration == 0) return;

            String groupName = tempName.remove(player.getUniqueId());

            if (groupName == null) return;

            PermissionGroup addedGroup = (PermissionGroup) Core.getInstance().getGroupManager().getAll().stream().filter( group -> group.getName().equals(groupName)).findFirst().orElse(null);

            if (addedGroup == null) {
                TextComponent prefix = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "prefix").getString()));
                TextComponent message = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "error", "ownerNotFound").getString()));

                player.sendMessage(prefix.append(message));
                return;
            }

            owner.addGroup(addedGroup, duration);
            PaperSync.sendSync(player, owner);
            PrefixManager.update(owner);
            tempFrom.get(player.getUniqueId()).open();
            tempFrom.remove(player.getUniqueId());
        } else {
            event.setCancelled(true);
        }
    }

    private static final Map<UUID, Boolean> inputNamePlayer = new HashMap<>();
    private static final Map<UUID, Boolean> inputDurationPlayer = new HashMap<>();

    private final Map<UUID, String> tempName = new HashMap<>();

    private static final Map<UUID, PaginatedMenu> tempFrom = new HashMap<>();

    public static void openNameAnvil(Player player, String name, PaginatedMenu from) {
        AnvilView anvil = MenuType.ANVIL.create(player, Component.text(name));

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.displayName(Component.text("Name Input"));
        paper.setItemMeta(meta);
        anvil.setItem(0, paper);

        inputNamePlayer.put(player.getUniqueId(), true);
        tempFrom.put(player.getUniqueId(), from);

        player.openInventory(anvil);
    }

    public static void openDurationAnvil(Player player, String name) {
        AnvilView anvil = MenuType.ANVIL.create(player, Component.text(name));

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.displayName(Component.text("Duration Input: 30m/5s/10h/7d/P"));
        paper.setItemMeta(meta);
        anvil.setItem(0, paper);

        inputDurationPlayer.put(player.getUniqueId(), true);

        player.openInventory(anvil);
    }
}
