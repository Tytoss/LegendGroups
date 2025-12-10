package de.tytoss.paper.menu.menus;

import de.tytoss.core.Core;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import de.tytoss.paper.menu.menus.input.GroupCreateAnvil;
import de.tytoss.paper.menu.menus.input.PlayerAddGroupAnvil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupsMenu extends PaginatedMenu {
    public GroupsMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility, from);
    }

    private final String groupsAddItemName = LegendGroups.configManager.get().node("message", "gui", "groupsAddItemName").getString();
    private final String removeLore = LegendGroups.configManager.get().node("message", "lore", "removeLore").getString();

    @Override
    public List<ItemStack> dataToItems() {
        List<ItemStack> items = new ArrayList<>();

        ItemStack addItem = makeItem(Material.HOPPER, groupsAddItemName, 0, "");
        items.add(addItem);

        for (PermissionOwner group : Core.getInstance().getGroupManager().getAll()) {
            ItemStack item = makeItem(Material.CHEST, group.getName(), 0, removeLore);
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
        return "§c§lGroups";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();


        if (item != null && !isBorderOrPageItem(event.getRawSlot()) && event.getClickedInventory() != player.getInventory()) {

            if (item.getType() == Material.HOPPER) {
                GroupCreateAnvil.openAnvil(player, getMenuName(), this);
                return;
            }

            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String displayName = serializer.serialize(item.displayName()).replace("[", "").replace("]", "");

            PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
            PermissionOwnerMenu permissionOwnerMenu = new PermissionOwnerMenu(playerMenuUtility, this, displayName);

            permissionOwnerMenu.open();
        }
    }
}
