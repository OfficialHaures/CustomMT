package nl.inferno.customMT.Roles.Politie;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PolitieBookListener implements Listener {
    private final Map<UUID, String> writingFine = new HashMap<>();
    private final Map<UUID, String> writingLicense = new HashMap<>();
    private final Map<UUID, String> writingReport = new HashMap<>();

    @EventHandler
    public void onBookUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().getDisplayName().equals("§6Politie boek")) return;

        openPoliceMenu(player);
        event.setCancelled(true);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("§6Politie")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String title = event.getView().getTitle();
        String itemName = clicked.getItemMeta().getDisplayName();

        switch (title) {
            case "§6Politie Menu" -> handleMainMenu(player, itemName);
            case "§6Boete Menu" -> handleFineMenu(player, itemName);
            case "§6Vergunning Menu" -> handleLicenseMenu(player, itemName);
            case "§6Rapport Menu" -> handleReportMenu(player, itemName);
        }
    }

    private void handleMainMenu(Player player, String itemName) {
        switch (itemName) {
            case "§cBoete Schrijven" -> openFineMenu(player);
            case "§aVergunning" -> openLicenseMenu(player);
            case "§eRapport" -> openReportMenu(player);
            case "§6Geschiedenis" -> showHistory(player);
        }
    }

    private void openPoliceMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§6Politie Menu");

        menu.setItem(10, createMenuItem(Material.PAPER, "§cBoete Schrijven", "§7Schrijf een boete uit"));
        menu.setItem(12, createMenuItem(Material.MAP, "§aVergunning", "§7Maak een vergunning"));
        menu.setItem(14, createMenuItem(Material.BOOK, "§eRapport", "§7Schrijf een rapport"));
        menu.setItem(16, createMenuItem(Material.BOOKSHELF, "§6Geschiedenis", "§7Bekijk alle documenten"));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    private void openFineMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 36, "§6Boete Menu");

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target != player) {
                menu.addItem(createMenuItem(Material.PLAYER_HEAD, "§c" + target.getName(),
                        "§7Klik om een boete uit te schrijven"));
            }
        }

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    private void openLicenseMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§6Vergunning Menu");

        menu.setItem(11, createMenuItem(Material.DIAMOND_PICKAXE, "§aMijn Vergunning",
                "§7Voor het mijnen van ertsen"));
        menu.setItem(13, createMenuItem(Material.FISHING_ROD, "§aVis Vergunning",
                "§7Voor het vissen"));
        menu.setItem(15, createMenuItem(Material.IRON_AXE, "§aHouthak Vergunning",
                "§7Voor het kappen van bomen"));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    private void openReportMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§6Rapport Menu");

        menu.setItem(11, createMenuItem(Material.PAPER, "§eNieuw Rapport",
                "§7Schrijf een nieuw rapport"));
        menu.setItem(15, createMenuItem(Material.BOOK, "§6Bekijk Rapporten",
                "§7Bekijk alle rapporten"));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    private void handleFineMenu(Player player, String itemName) {
        String targetName = itemName.substring(2);
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            writingFine.put(player.getUniqueId(), targetName);
            player.closeInventory();
            player.sendMessage("§aTyp het boete bedrag in de chat:");
        }
    }

    private void handleLicenseMenu(Player player, String itemName) {
        switch (itemName) {
            case "§aMijn Vergunning" -> giveLicense(player, "MINING");
            case "§aVis Vergunning" -> giveLicense(player, "FISHING");
            case "§aHouthak Vergunning" -> giveLicense(player, "WOODCUTTING");
        }
    }

    private void handleReportMenu(Player player, String itemName) {
        if (itemName.equals("§eNieuw Rapport")) {
            player.closeInventory();
            player.sendMessage("§aTyp je rapport in de chat:");
            writingReport.put(player.getUniqueId(), "");
        } else if (itemName.equals("§6Bekijk Rapporten")) {
            showReports(player);
        }
    }

    private void giveLicense(Player player, String type) {
        player.sendMessage("§aJe hebt een " + type + " vergunning uitgegeven!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.closeInventory();
    }

    private void showReports(Player player) {
        player.sendMessage("§6Laatste rapporten:");
        // Here you would load and show reports from a database
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    private void showHistory(Player player) {
        player.sendMessage("§6Geschiedenis van documenten:");
        // Here you would load and show all documents from a database
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
