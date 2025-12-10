package de.tytoss.paper.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        if (!(e.getInventory().getHolder() instanceof Menu holder)) {
            return;
        }

        if (e.getCurrentItem() == null) {
            return;
        }

        try {
            holder.handleMenu(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (holder.cancelAllClicks()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e) {

        if (!(e.getInventory().getHolder() instanceof Menu holder)) {
            return;
        }

        holder.handleMenuClose();
    }
}
