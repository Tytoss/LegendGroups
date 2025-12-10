package de.tytoss.paper.menu.menus;

import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import de.tytoss.paper.menu.menus.ownerMenus.OwnerGroupsMenu;
import de.tytoss.paper.menu.menus.ownerMenus.OwnerPermissionsMenu;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PermissionOwnerMenu extends PaginatedMenu {

    private final String OWNER_NAME;

    private final String ownerGroupsItemName = LegendGroups.configManager.get().node("message", "gui", "ownerGroupsItemName").getString();
    private final String ownerPermissionsItemName = LegendGroups.configManager.get().node("message", "gui", "ownerPermissionsItemName").getString();

    public PermissionOwnerMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from, String ownerName) {
        super(playerMenuUtility, from);
        this.OWNER_NAME = ownerName;
    }

    @Override
    public List<ItemStack> dataToItems() {
        ItemStack groupItem = makeItem(Material.CHEST, ownerGroupsItemName, 0, "");
        ItemStack permissionItem = makeItem(Material.CHEST, ownerPermissionsItemName, 0, "");
        return List.of(groupItem, permissionItem);
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
        return OWNER_NAME;
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        ItemStack item = event.getCurrentItem();

        if (item != null && !isBorderOrPageItem(event.getRawSlot()) && event.getClickedInventory() != player.getInventory()) {
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String displayName = serializer.serialize(item.displayName()).replace("[", "").replace("]", "");

            if (displayName.equals(ownerGroupsItemName)) {
                PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
                OwnerGroupsMenu groupsMenu = new OwnerGroupsMenu(playerMenuUtility, this);
                groupsMenu.open();
            } else if (displayName.equals(ownerPermissionsItemName)) {
                PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
                OwnerPermissionsMenu permissionsMenu = new OwnerPermissionsMenu(playerMenuUtility, this);
                permissionsMenu.open();
            }
        }
    }
}
