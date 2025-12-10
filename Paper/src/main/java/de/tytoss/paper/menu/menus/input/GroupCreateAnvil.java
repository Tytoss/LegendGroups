package de.tytoss.paper.menu.menus.input;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.messenger.PaperSync;
import net.kyori.adventure.text.Component;
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
import java.util.UUID;

public class GroupCreateAnvil implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView() instanceof AnvilView inv)) return;

        Player player = (Player) event.getWhoClicked();

        if (inputNamePlayer.remove(player.getUniqueId()) != null) {
            event.setCancelled(true);

            String groupName = inv.getRenameText();

            if (groupName == null) return;

            PermissionGroup group = (PermissionGroup) Core.getInstance().getGroupManager().create(UUID.randomUUID(), groupName);

            PaperSync.sendSync(player, group);
            tempFrom.get(player.getUniqueId()).open();
            tempFrom.remove(player.getUniqueId());
        }
    }

    private static final Map<UUID, Boolean> inputNamePlayer = new HashMap<>();
    private static final Map<UUID, PaginatedMenu> tempFrom = new HashMap<>();

    public static void openAnvil(Player player, String name, PaginatedMenu from) {
        AnvilView anvil = MenuType.ANVIL.create(player, Component.text(name));

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.displayName(Component.text("Name"));
        paper.setItemMeta(meta);
        anvil.setItem(0, paper);

        inputNamePlayer.put(player.getUniqueId(), true);
        tempFrom.put(player.getUniqueId(), from);

        player.openInventory(anvil);
    }
}
