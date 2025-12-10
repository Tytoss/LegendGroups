package de.tytoss.paper.menu.menus;

import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.Menu;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class MainMenu extends PaginatedMenu {

    private final String groupItemName = LegendGroups.configManager.get().node("message", "gui", "groupItemName").getString();
    private final String playerItemName = LegendGroups.configManager.get().node("message", "gui", "playerItemName").getString();

    public MainMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility, from);
    }

    @Override
    public List<ItemStack> dataToItems() {
        ItemStack item1 = makeItem(Material.CHEST, groupItemName, 0, "");
        ItemStack item2 = makeItem(Material.PLAYER_HEAD, playerItemName, 0, "");

        return List.of(item1, item2);
    }

    @Override
    public Map<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }

    @Override
    public Integer maxPages() {
        return 1;
    }

    @Override
    public String getMenuName() {
        return "";
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

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();

        if (item != null && !isBorderOrPageItem(event.getRawSlot()) && event.getClickedInventory() != player.getInventory()) {
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String displayName = serializer.serialize(item.displayName()).replace("[", "").replace("]", "");

            if (displayName.equals(playerItemName)) {
                PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
                PlayerMenu playerMenu = new PlayerMenu(playerMenuUtility, this);
                playerMenu.open();
            } else if (displayName.equals(groupItemName)) {
                PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
                GroupsMenu groupsMenu = new GroupsMenu(playerMenuUtility, this);
                groupsMenu.open();
            }
        }
    }
}
