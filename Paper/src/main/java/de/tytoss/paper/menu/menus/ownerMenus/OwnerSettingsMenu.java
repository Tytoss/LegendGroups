package de.tytoss.paper.menu.menus.ownerMenus;

import de.tytoss.core.Core;
import de.tytoss.core.entity.PermissionGroup;
import de.tytoss.core.entity.base.PermissionOwner;
import de.tytoss.core.manager.base.OwnerManager;
import de.tytoss.core.metadata.keys.MetaKeys;
import de.tytoss.paper.LegendGroups;
import de.tytoss.paper.menu.PaginatedMenu;
import de.tytoss.paper.menu.PlayerMenuUtility;
import de.tytoss.paper.menu.menus.input.PermissionAddAnvil;
import de.tytoss.paper.menu.menus.input.PrefixAnvil;
import de.tytoss.paper.menu.menus.input.WeightAnvil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OwnerSettingsMenu extends PaginatedMenu {
    public OwnerSettingsMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility, from);
    }

    @Override
    public List<ItemStack> dataToItems() {
        Player p = Bukkit.getPlayer(from.getMenuName());

        PermissionOwner owner;

        if (p == null) {
            owner = Core.getInstance().getGroupManager().getAll().stream().filter( group -> Objects.equals(group.getName(), from.getMenuName())).findFirst().orElse(null);
        } else {
            owner = Core.getInstance().getPlayerManager().get(p.getUniqueId());
        }

        if (owner == null) return List.of();

        ItemStack prefixItem = makeItem(Material.NAME_TAG, "§cPrefix", 0, owner.getPrefix());
        ItemStack weightItem = makeItem(Material.ANVIL, "§cWeight", 0, owner.getMetaData().getMeta(MetaKeys.WEIGHT).getFirst().getValue().toString());
        return List.of(prefixItem, weightItem);
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

        Player target = Bukkit.getPlayer(from.getMenuName());

        PermissionOwner owner;

        if (target != null) {
            owner = Core.getInstance().getPlayerManager().get(target.getUniqueId());
        } else {
            owner = Core.getInstance().getGroupManager().getAll().stream().filter( group -> group.getName().equals(from.getMenuName())).findFirst().orElse(null);
        }

        if (owner == null) {
            TextComponent prefix = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "prefix").getString()));
            TextComponent message = Component.text(Objects.requireNonNull(LegendGroups.configManager.get().node("message", "error", "ownerNotFound").getString()));

            this.player.sendMessage(prefix.append(message));
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null) {
            player.closeInventory();
            return;
        }

        if (item.getType() == Material.NAME_TAG) {
            PrefixAnvil.openAnvil(player, getMenuName(), this);
        }

        if (item.getType() == Material.ANVIL) {
            WeightAnvil.openAnvil(player, getMenuName(), this);
        }
    }
}
