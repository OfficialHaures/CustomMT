package nl.inferno.customMT.Roles.Gemeente;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GemeenteListener implements Listener {
    private final GemeenteDuty gemeenteDuty;
    private final Map<UUID, BlockVector3> pos1 = new HashMap<>();
    private final Map<UUID, BlockVector3> pos2 = new HashMap<>();

    public GemeenteListener(GemeenteDuty gemeenteDuty) {
        this.gemeenteDuty = gemeenteDuty;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!player.hasPermission("custommt.gemeente")) return;
        if (item.getType() == Material.EMERALD || !item.hasItemMeta()) return;

        String itemName = item.getItemMeta().getDisplayName();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (itemName) {
                case "§6Plot Manager" -> {
                    if (player.hasPermission("custommt.gemeente.plotmanager")) {
                        GemeentePlotManager.openPlotMenu(player);
                    }
                }
                case "§6Plot Informatie" -> {
                    if (player.hasPermission("custommt.gemeente.plotinfo")) {
                        showPlotInfo(player);
                    }
                }
                case "§aPlot Aanmaken" -> {
                    if (player.hasPermission("custommt.gemeente.plotcreate")) {
                        createPlot(player);
                    }
                }
            }
            event.setCancelled(true);
        }

        if (itemName.equals("§6Plot Selector") && player.hasPermission("custommt.gemeente.plotselect")) {
            handlePlotSelection(event, player);
        }
    }

    private void handlePlotSelection(PlayerInteractEvent event, Player player) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
            pos1.put(player.getUniqueId(), BlockVector3.at(
                    event.getClickedBlock().getX(),
                    event.getClickedBlock().getY(),
                    event.getClickedBlock().getZ()
            ));
            player.sendMessage("§aPunt 1 geselecteerd!");
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            pos2.put(player.getUniqueId(), BlockVector3.at(
                    event.getClickedBlock().getX(),
                    event.getClickedBlock().getY(),
                    event.getClickedBlock().getZ()
            ));
            player.sendMessage("§aPunt 2 geselecteerd!");
            event.setCancelled(true);
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
                case "§aPlot Aanmaken" -> createPlot(player);
                case "§cPlot Verwijderen" -> deletePlot(player);
                case "§ePlot Informatie" -> showPlotInfo(player);
                case "§6Alle Plots" -> showAllPlots(player);
            }
        }
    }

    private void createPlot(Player player) {
        if (!pos1.containsKey(player.getUniqueId()) || !pos2.containsKey(player.getUniqueId())) {
            player.sendMessage("§cSelecteer eerst twee punten met de Plot Selector!");
            return;
        }

        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));

        if (regions == null) return;

        String plotName = "plot_" + UUID.randomUUID().toString().substring(0, 8);
        ProtectedRegion region = new ProtectedCuboidRegion(plotName,
                pos1.get(player.getUniqueId()),
                pos2.get(player.getUniqueId()));

        regions.addRegion(region);
        player.sendMessage("§aPlot succesvol aangemaakt met ID: " + plotName);

        pos1.remove(player.getUniqueId());
        pos2.remove(player.getUniqueId());
    }

    private void deletePlot(Player player) {
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));

        if (regions == null) return;

        ProtectedRegion region = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()))
                .getRegions().stream().findFirst().orElse(null);

        if (region == null) {
            player.sendMessage("§cJe moet op een plot staan!");
            return;
        }

        regions.removeRegion(region.getId());
        player.sendMessage("§aPlot succesvol verwijderd!");
    }

    private void showPlotInfo(Player player) {
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));

        if (regions == null) return;

        ProtectedRegion region = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()))
                .getRegions().stream().findFirst().orElse(null);

        if (region == null) {
            player.sendMessage("§cJe staat niet op een plot!");
            return;
        }

        player.sendMessage("§6=== Plot Informatie ===");
        player.sendMessage("§7ID: §f" + region.getId());
        player.sendMessage("§7Eigenaren: §f" + String.join(", ", region.getOwners().getPlayers()));
        player.sendMessage("§7Leden: §f" + String.join(", ", region.getMembers().getPlayers()));
    }

    private void showAllPlots(Player player) {
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));

        if (regions == null) return;

        player.sendMessage("§6=== Alle Plots ===");
        regions.getRegions().forEach((id, region) ->
                player.sendMessage("§7- §f" + id + " §7(Eigenaren: §f" +
                        String.join(", ", region.getOwners().getPlayers()) + "§7)")
        );
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
