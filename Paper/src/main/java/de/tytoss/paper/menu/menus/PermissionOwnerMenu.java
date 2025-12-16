package de.tytoss.paper.menu.menus;

import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import de.tytoss.paper.menu.menus.ownerMenus.OwnerGroupsMenu;
import de.tytoss.paper.menu.menus.ownerMenus.OwnerPermissionsMenu;
import de.tytoss.paper.menu.menus.ownerMenus.OwnerSettingsMenu;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PermissionOwnerMenu extends PaginatedMenu {

    private final String OWNER_NAME;

    private final String ownerGroupsItemName = LegendGroups.configManager.get().node("message", "gui", "ownerGroupsItemName").getString();
    private final String ownerPermissionsItemName = LegendGroups.configManager.get().node("message", "gui", "ownerPermissionsItemName").getString();
    private final String ownerSettingsItemName = LegendGroups.configManager.get().node("message", "gui", "ownerSettingsItemName").getString();

    public PermissionOwnerMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from, String ownerName) {
        super(playerMenuUtility, from);
        this.OWNER_NAME = ownerName;
    }

    @Override
    public List<ItemStack> dataToItems() {
        Player p = Bukkit.getPlayer(OWNER_NAME);

        ItemStack groupItem = makeItem(Material.CHEST, ownerGroupsItemName, 0, "");
        ItemStack permissionItem = makeItem(Material.CHEST, ownerPermissionsItemName, 0, "");
        if (p == null) {
            ItemStack settingsItem = makeItem(Material.CHEST, ownerSettingsItemName, 0, "");
            return List.of(groupItem, permissionItem, settingsItem);
        }
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
            } else if (displayName.equals(ownerSettingsItemName)) {
                PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
                OwnerSettingsMenu settingsMenu = new OwnerSettingsMenu(playerMenuUtility, this);
                settingsMenu.open();
            }
        }
    }
}
