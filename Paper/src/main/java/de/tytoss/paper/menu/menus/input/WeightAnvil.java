package de.tytoss.paper.menu.menus.input;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.PermissionPlayer;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.messenger.PaperSync;
import de.tytoss.paper.prefix.PrefixManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

public class WeightAnvil implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView() instanceof AnvilView inv)) return;

        Player player = (Player) event.getWhoClicked();

        if (inputPermissionPlayer.remove(player.getUniqueId()) != null && event.getRawSlot() == 2) {
            event.setCancelled(true);

            String weight = inv.getRenameText();

            if (weight == null) return;

            System.out.println(weight);

            Integer parsedWeight = Integer.parseInt(weight);

            System.out.println(parsedWeight);

            PaginatedMenu from = tempFrom.get(player.getUniqueId());

            Player target = Bukkit.getPlayer(from.getMenuName());

            PermissionOwner weightTarget;

            if (target == null) {
                weightTarget = Core.getInstance().getGroupManager().getAll().stream().filter( group -> group.getName().equals(from.getMenuName())).findFirst().orElse(null);

                if(weightTarget == null) return;

                weightTarget.getMetaData().setMeta(MetaKeys.WEIGHT, parsedWeight);

                for (PermissionOwner permissionOwner : Core.getInstance().getPlayerManager().getAll()) {
                    PermissionPlayer permissionPlayer = (PermissionPlayer) permissionOwner;
                    if (permissionPlayer.getGroups().contains((PermissionGroup) weightTarget)) {
                        PrefixManager.update(permissionPlayer);
                    }
                }
            } else {
                weightTarget = Core.getInstance().getPlayerManager().get(target.getUniqueId());

                if(weightTarget == null) return;

                weightTarget.getMetaData().setMeta(MetaKeys.WEIGHT, parsedWeight);

                for (PermissionOwner owner : Core.getInstance().getPlayerManager().getAll()) {
                    PrefixManager.update((PermissionPlayer) owner);
                }
            }

            PaperSync.sendSync(player, weightTarget);
            from.open();
            tempFrom.remove(player.getUniqueId());
        } else {
            System.out.println("WORST CASE");
            event.setCancelled(true);
        }
    }

    private static final Map<UUID, Boolean> inputPermissionPlayer = new HashMap<>();
    private static final Map<UUID, PaginatedMenu> tempFrom = new HashMap<>();

    public static void openAnvil(Player player, String name, PaginatedMenu from) {
        AnvilView anvil = MenuType.ANVIL.create(player, Component.text(name));

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.displayName(Component.text("Weight"));
        paper.setItemMeta(meta);
        anvil.setItem(0, paper);

        inputPermissionPlayer.put(player.getUniqueId(), true);
        tempFrom.put(player.getUniqueId(), from);

        player.openInventory(anvil);
    }
}
