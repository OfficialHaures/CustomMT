package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GemeentePlotListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Plot Manager")) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch (itemName) {
            case "§aVoeg Eigenaar Toe" -> player.sendMessage("§aKies een speler om toe te voegen");
            case "§cVerwijder Eigenaar" -> player.sendMessage("§cKies een eigenaar om te verwijderen");
            case "§aVoeg Lid Toe" -> player.sendMessage("§aKies een speler om als lid toe te voegen");
            case "§6Plot Informatie" -> player.sendMessage("§6Loading plot info...");
        }
    }
}
