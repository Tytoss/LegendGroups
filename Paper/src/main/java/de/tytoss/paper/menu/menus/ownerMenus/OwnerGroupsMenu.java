package de.tytoss.paper.menu.menus.ownerMenus;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import de.tytoss.paper.menu.menus.input.PlayerAddGroupAnvil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class OwnerGroupsMenu extends PaginatedMenu {
    public OwnerGroupsMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility, from);
    }

    private NamespacedKey key = new NamespacedKey(LegendGroups.getInstance(), "legendGroupsOwnerGroupsMenu");

    private final String groupsAddItemName = LegendGroups.configManager.get().node("message", "gui", "groupsAddItemName").getString();
    private final String removeLore = LegendGroups.configManager.get().node("message", "lore", "removeLore").getString();
    @Override
    public List<ItemStack> dataToItems() {
        Player player = Bukkit.getPlayer(from.getMenuName());
        Set<PermissionGroup> groups;
        if (player != null) {
            PermissionPlayer owner = (PermissionPlayer) Core.getInstance().getPlayerManager().get(player.getUniqueId());

            groups = owner.getGroups();
        } else {
            PermissionGroup owner = (PermissionGroup) Core.getInstance().getGroupManager().getAll().stream().filter( group -> group.getName().equals(from.getMenuName())).findFirst().orElse(null);

            if (owner == null) {
                this.player.closeInventory();

                TextComponent prefix = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "prefix").getString()));
                TextComponent message = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "error", "ownerNotFound").getString()));

                this.player.sendMessage(prefix.append(message));

                return Collections.emptyList();
            }

            groups = owner.getInheritedGroups();
        }

        List<ItemStack> items = new ArrayList<>();

        ItemStack addItem = makeItem(Material.HOPPER, groupsAddItemName, 0, "");
        items.add(addItem);

        for (PermissionGroup group : groups) {
            ItemStack item = makeItem(Material.CHEST, group.getName(), 0, removeLore);
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, group.getId().toString());
            item.setItemMeta(meta);
            items.add(item);
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getCustomMenuBorderItems() {
        return Map.of();
    }

    @Override
    public Integer maxPages() {
        return 0;
    }

    @Override
    public String getMenuName() {
        return from.getMenuName();
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        Player player = (Player) event.getWhoClicked();

        Player target = Bukkit.getPlayer(from.getMenuName());

        if (target == null) return;

        PermissionPlayer owner = (PermissionPlayer) Core.getInstance().getPlayerManager().get(target.getUniqueId());

        ItemStack item = event.getCurrentItem();

        if (item == null) {
            player.closeInventory();
            return;
        }

        if (item.getType() == Material.HOPPER) {
            PlayerAddGroupAnvil.openNameAnvil(player, getMenuName(), this);
        }

        if (item.getType() == Material.CHEST && event.isRightClick()) {
            UUID groupUUID = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
            PermissionGroup group = (PermissionGroup) Core.getInstance().getGroupManager().get(groupUUID);

            owner.removeGroup(group);

            refreshData();
        }
    }
}
