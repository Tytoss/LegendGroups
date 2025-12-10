package de.tytoss.paper.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class Menu implements InventoryHolder {
    protected final PlayerMenuUtility playerMenuUtility;
    protected final Player player;
    protected Inventory menuInventory;

    protected final ItemStack FILLER_GLASS = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ", 0);

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
        this.player = playerMenuUtility.getOwner();
    }

    public abstract String getMenuName();
    public abstract int getSlots();
    public abstract boolean cancelAllClicks();
    public abstract void onClick(InventoryClickEvent e);
    public abstract void setMenuItems();

    public void handleMenu(InventoryClickEvent e) {
        onClick(e);
    }

    public void open() {
        menuInventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        setMenuItems();
        playerMenuUtility.getOwner().openInventory(menuInventory);
        playerMenuUtility.pushMenu(this);
    }

    public void back() {
        Menu last = playerMenuUtility.lastMenu();
        if (last != null) {
            MenuManager.openMenu(last.getClass(), playerMenuUtility.getOwner());
        }
    }

    protected void reloadItems() {
        for (int i = 0; i < menuInventory.getSize(); i++) {
            menuInventory.setItem(i, null);
        }
        setMenuItems();
    }

    protected void reload() {
        player.closeInventory();
        MenuManager.openMenu(this.getClass(), player);
    }

    @Override
    public Inventory getInventory() {
        return menuInventory;
    }

    public void setFillerGlass() {
        for (int i = 0; i < getSlots(); i++) {
            if (menuInventory.getItem(i) == null) {
                menuInventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    public void setFillerGlass(ItemStack itemStack) {
        for (int i = 0; i < getSlots(); i++) {
            if (menuInventory.getItem(i) == null) {
                menuInventory.setItem(i, itemStack);
            }
        }
    }

    public ItemStack makeItem(Material material, String displayName, int customModel, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.lore(List.of(Component.text(Arrays.toString(lore).replace("[", "").replace("]", ""))));

        meta.displayName(Component.text(displayName));
        if (customModel != 0) meta.setCustomModelData(customModel);

        item.setItemMeta(meta);
        return item;
    }

    public void handleMenuClose() {}
}
