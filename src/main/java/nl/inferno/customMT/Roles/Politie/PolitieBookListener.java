package nl.inferno.customMT.Roles.Politie;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

public class PolitieBookListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§9Politie Boek")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch(itemName) {
            case "§cBoete Schrijven" -> PolitieBookGUI.openWriteBook(player, "FINE");
            case "§aVergunning Schrijven" -> PolitieBookGUI.openWriteBook(player, "LICENSE");
            case "§eRapport Schrijven" -> PolitieBookGUI.openWriteBook(player, "REPORT");
            case "§6Bekijk Geschiedenis" -> showHistory(player);
        }
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        BookMeta meta = event.getNewBookMeta();
        if (!meta.hasTitle()) return;

        Player player = event.getPlayer();
        String content = String.join("\n", meta.getPages());

        switch(meta.getTitle()) {
            case "§cBoete" -> saveFine(player, content);
            case "§aVergunning" -> saveLicense(player, content);
            case "§eRapport" -> saveReport(player, content);
        }
    }

    private void saveFine(Player player, String content) {
        // Save to database
        player.sendMessage("§aBoete succesvol opgeslagen!");
    }

    private void saveLicense(Player player, String content) {
        // Save to database
        player.sendMessage("§aVergunning succesvol opgeslagen!");
    }

    private void saveReport(Player player, String content) {
        // Save to database
        player.sendMessage("§aRapport succesvol opgeslagen!");
    }

    private void showHistory(Player player) {
        // Load from database and show
        player.sendMessage("§6Loading geschiedenis...");
    }
}
