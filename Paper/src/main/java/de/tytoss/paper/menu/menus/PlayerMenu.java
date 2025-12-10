package de.tytoss.paper.menu.menus;

import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerMenu extends PaginatedMenu {
    public PlayerMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility, from);
    }

    @Override
    public List<ItemStack> dataToItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            meta.setOwningPlayer(player);
            meta.setDisplayName("§e" + player.getName());
            head.setItemMeta(meta);
            items.add(head);
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
        return "§c§lPlayer";
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
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            OfflinePlayer target = meta.getOwningPlayer();

            if (target != null) {
                PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
                PermissionOwnerMenu permissionOwnerMenu = new PermissionOwnerMenu(playerMenuUtility, this, target.getName());

                permissionOwnerMenu.open();
            }
        }
    }
}
