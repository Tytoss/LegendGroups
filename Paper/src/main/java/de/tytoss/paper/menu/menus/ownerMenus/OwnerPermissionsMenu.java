package de.tytoss.paper.menu.menus.ownerMenus;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.metadata.MetaData;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class OwnerPermissionsMenu extends PaginatedMenu {
    public OwnerPermissionsMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility, from);
    }

    private final String permissionsAddItemName = LegendGroups.configManager.get().node("message", "gui", "permissionsAddItemName").getString();
    private final String removeLore = LegendGroups.configManager.get().node("message", "lore", "removeLore").getString();

    @Override
    public List<ItemStack> dataToItems() {
        PermissionOwner owner = Core.getInstance().getPlayerManager().get(player.getUniqueId());
        Set<String> permissions = owner.getMetaData().getAll().stream()
                .filter(meta -> meta.getKey().startsWith(MetaKeys.PERMISSIONS))
                .filter(meta -> !meta.isExpired())
                .map(meta -> meta.getKey().replace(MetaKeys.PERMISSIONS, ""))
                .collect(Collectors.toSet());

        List<ItemStack> items = new ArrayList<>();

        ItemStack addItem = makeItem(Material.HOPPER, permissionsAddItemName, 0, "");
        items.add(addItem);

        for (String permission : permissions) {
            ItemStack item = makeItem(Material.NAME_TAG, permission, 0, removeLore);
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
}

