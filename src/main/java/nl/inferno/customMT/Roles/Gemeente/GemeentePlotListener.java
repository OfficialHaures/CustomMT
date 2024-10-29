package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GemeentePlotListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Plot Beheer")) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch (itemName) {
            case "§aPlot Aanmaken" -> player.sendMessage("§aSelecteer eerst twee punten met de plot wand!");
            case "§cPlot Verwijderen" -> player.sendMessage("§eJe staat op plot: [plotnaam]");
            case "§ePlot Informatie" -> player.sendMessage("§6Loading plot info...");
            case "§6Alle Plots" -> player.sendMessage("§ePlots worden geladen...");
        }
    }
}
