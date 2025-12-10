package de.tytoss.paper.menu;

import de.tytoss.paper.LegendGroups;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class PaginatedMenu extends Menu {

    protected List<Object> data = new ArrayList<>();
    protected int page = 0;

    protected PaginatedMenu from;
    protected int maxItemsPerPage = switch (getSlots()) {
        case 18 -> 7;
        case 27 -> 7;
        case 36 -> 14;
        case 45 -> 21;
        case 54 -> 28;
        default -> throw new IllegalStateException("Unexpected value: " + getInventory().getSize());
    };

    private List<ItemStack> cachedItems = null;

    private final String firstPage = LegendGroups.configManager.get().node("message", "gui", "firstPage").getString();
    private final String prevPage = LegendGroups.configManager.get().node("message", "gui", "previousPage").getString();
    private final String nextPage = LegendGroups.configManager.get().node("message", "gui", "nextPage").getString();
    private final String lastPage = LegendGroups.configManager.get().node("message", "gui", "lastPage").getString();
    private final String exit = LegendGroups.configManager.get().node("message", "gui", "exit").getString();
    private final String goBack = LegendGroups.configManager.get().node("message", "gui", "return").getString();

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, PaginatedMenu from) {
        super(playerMenuUtility);
        this.from = from;
    }

    public abstract List<ItemStack> dataToItems();

    public abstract Map<Integer, ItemStack> getCustomMenuBorderItems();

    public abstract Integer maxPages();

    protected void addMenuBorder() {
        int items = getItems().size();

        int size = getInventory().getSize();
        int lastRowStart = size - 9;

        for (int i = 0; i <= 9; i++) {
            if (getInventory().getItem(i) == null) getInventory().setItem(i, FILLER_GLASS);
        }

        int[] glassSlots = new int[0];

        glassSlots = switch (getSlots()) {
            case 18 -> new int[]{17};
            case 27 -> new int[]{17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
            case 36 -> new int[]{17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
            case 45 -> new int[]{17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
            case 54 -> new int[]{17, 18, 26, 27, 35, 36};
            case 0 -> new int[]{};
            default -> glassSlots;
        };

        for (int slot : glassSlots) {
            if (slot < getInventory().getSize()) {
                getInventory().setItem(slot, FILLER_GLASS);
            }
        }

        for (int i = 44; i <= 53; i++) {
            if (i < getInventory().getSize()) {
                if (getInventory().getItem(i) == null) getInventory().setItem(i, FILLER_GLASS);
            }
        }

        if (this.from != null) getInventory().setItem(lastRowStart, makeItem(Material.PAPER, ChatColor.GREEN + goBack, 0));

        if (items > maxItemsPerPage) {
            getInventory().setItem(lastRowStart + 2, makeItem(Material.PAPER, ChatColor.GREEN + firstPage, 0));
            getInventory().setItem(lastRowStart + 3, makeItem(Material.PAPER, ChatColor.GREEN + prevPage, 0));
            getInventory().setItem(lastRowStart + 4, makeItem(Material.PAPER, ChatColor.DARK_RED + exit, 0));
            getInventory().setItem(lastRowStart + 5, makeItem(Material.PAPER, ChatColor.GREEN + nextPage, 0));
            getInventory().setItem(lastRowStart + 6, makeItem(Material.PAPER, ChatColor.GREEN + lastPage, 0));
        }

        Map<Integer, ItemStack> custom = getCustomMenuBorderItems();
        if (custom != null) {
            for (Map.Entry<Integer, ItemStack> entry : custom.entrySet()) {
                getInventory().setItem(entry.getKey(), entry.getValue());
            }
        }
    }

    protected List<ItemStack> getItems() {
        if (cachedItems == null) cachedItems = dataToItems();
        return cachedItems;
    }

    public boolean isBorderOrPageItem(int slot) {
        Set<Integer> borderSlots = switch (getInventory().getSize()) {
            case 9 -> new HashSet<>(Arrays.asList(
                    0,8
            ));
            case 18 -> new HashSet<>(Arrays.asList(
                    0,1,2,3,4,5,6,7,8,
                    9,17
            ));
            case 27 -> new HashSet<>(Arrays.asList(
                    0,1,2,3,4,5,6,7,8,
                    9,17,18,
                    19,20,21,22,23,24,25,26
            ));
            case 36 -> new HashSet<>(Arrays.asList(
                    0,1,2,3,4,5,6,7,8,
                    9,17,18,26,27,28,
                    29,30,31,32,33,34,35
            ));
            case 45 -> new HashSet<>(Arrays.asList(
                    0,1,2,3,4,5,6,7,8,
                    9,17,18,26,27,35,36,
                    37,38,39,40,41,42,43,44
            ));
            case 54 -> new HashSet<>(Arrays.asList(
                    0,1,2,3,4,5,6,7,8,9,
                    17,18,26,27,35,36,
                    44,45,46,47,48,49,50,51,52,53
            ));
            default -> throw new IllegalStateException("Unexpected value: " + getInventory().getSize());
        };

        return borderSlots.contains(slot);
    }

    protected void invalidateCache() {
        cachedItems = null;
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        List<ItemStack> items = getItems();

        int slot = 10;

        for (int i = 0; i < maxItemsPerPage; i++) {
            int index = maxItemsPerPage * page + i;
            if (index >= items.size()) break;

            if (slot % 9 == 8) slot += 2;

            getInventory().setItem(slot, items.get(index));
            slot++;
        }
    }

    public int getMaxPages() {
        Integer maxPages = maxPages();
        return maxPages != null ? maxPages : 0;
    }

    public boolean prevPage() {
        if (page == 0) return false;
        page--;
        reloadItems();
        return true;
    }

    public boolean nextPage() {
        int totalItems = getItems().size();
        int lastPageNumber = (totalItems - 1) / maxItemsPerPage;

        if (getMaxPages() != 0 && page >= getMaxPages()) return false;

        if (page < lastPageNumber) {
            page++;
            reloadItems();
            return true;
        }

        return false;
    }

    @Override
    public void open() {
        invalidateCache();
        super.open();
    }

    public void refreshData() {
        invalidateCache();
        reloadItems();
    }

    public boolean firstPage() {
        if (page == 0) return false;
        page = 0;
        reloadItems();
        return true;
    }

    public boolean lastPage() {
        int lastPageNum = (getItems().size() - 1) / maxItemsPerPage;
        if (page == lastPageNum) return false;
        page = lastPageNum;
        reloadItems();
        return true;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem == null) return;

        if (!currentItem.hasItemMeta()) return;
        if (!currentItem.getItemMeta().hasDisplayName()) return;

        String displayName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());

        if (displayName.equals(firstPage)) {
            firstPage();
        } else if (displayName.equals(prevPage)) {
            prevPage();
        } else if (displayName.equals(nextPage)) {
            nextPage();
        } else if (displayName.equals(lastPage)) {
            lastPage();
        } else if (displayName.equals(exit)) {
            player.closeInventory();
        } else if (displayName.equals(goBack)) {
            this.from.open();
        }
    }
}