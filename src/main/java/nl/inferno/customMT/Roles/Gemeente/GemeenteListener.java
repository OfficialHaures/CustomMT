package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GemeenteListener implements Listener {
    private final GemeenteDuty gemeenteDuty;

    public GemeenteListener(GemeenteDuty gemeenteDuty) {
        this.gemeenteDuty = gemeenteDuty;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!gemeenteDuty.isInDuty(player.getUniqueId())) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        String itemName = item.getItemMeta().getDisplayName();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (itemName) {
                case "§6Plot Manager" -> GemeentePlotManager.openPlotMenu(player);
                case "§6Plot Informatie" -> player.sendMessage("§6Plot informatie wordt geladen...");
                case "§6Plot Aanmaken" -> player.sendMessage("§aKies een naam voor het plot:");
            }
            event.setCancelled(true);
        }

        if (itemName.equals("§6Plot Selector")) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                player.sendMessage("§aPunt 1 geselecteerd!");
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage("§aPunt 2 geselecteerd!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!gemeenteDuty.isInDuty(player.getUniqueId())) {
            return;
        }

        String title = event.getView().getTitle();
        if (title.equals("§6Plot Beheer")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();
            switch (itemName) {
                case "§aPlot Aanmaken" -> {
                    player.closeInventory();
                    player.sendMessage("§aGebruik de Plot Selector om twee punten te selecteren!");
                }
                case "§cPlot Verwijderen" -> {
                    player.closeInventory();
                    player.sendMessage("§eWeet je zeker dat je dit plot wilt verwijderen?");
                }
                case "§ePlot Informatie" -> {
                    player.sendMessage("§6Eigenaar: §7[naam]");
                    player.sendMessage("§6Grootte: §7[grootte]");
                }
                case "§6Alle Plots" -> player.sendMessage("§aLijst van alle plots wordt geladen...");
            }
        }
    }

    @EventHandler
    public void onArmorEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!gemeenteDuty.isInDuty(player.getUniqueId())) {
            return;
        }

        if (event.getSlotType().toString().contains("ARMOR")) {
            event.setCancelled(true);
        }
    }
}
