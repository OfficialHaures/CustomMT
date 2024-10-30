package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GemeenteGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Plot Manager")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch (itemName) {
            case "§aVoeg Eigenaar Toe" -> {
                if (player.hasPermission("custommt.gemeente.addowner")) {
                    player.closeInventory();
                    player.sendMessage("§aTyp de naam van de nieuwe eigenaar in de chat:");
                }
            }
            case "§cVerwijder Eigenaar" -> {
                if (player.hasPermission("custommt.gemeente.removeowner")) {
                    player.closeInventory();
                    player.sendMessage("§cTyp de naam van de te verwijderen eigenaar in de chat:");
                }
            }
            case "§aVoeg Lid Toe" -> {
                if (player.hasPermission("custommt.gemeente.addmember")) {
                    player.closeInventory();
                    player.sendMessage("§aTyp de naam van het nieuwe lid in de chat:");
                }
            }
            case "§6Plot Informatie" -> {
                if (player.hasPermission("custommt.gemeente.plotinfo")) {
                    showPlotInfo(player);
                }
            }
        }
    }

    private void showPlotInfo(Player player) {
        player.sendMessage("§6=== Plot Informatie ===");
        player.sendMessage("§7ID: §fplot_123");
        player.sendMessage("§7Eigenaar: §fSpeler123");
        player.sendMessage("§7Leden: §fLid1, Lid2");
    }
}
