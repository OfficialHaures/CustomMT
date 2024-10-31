package nl.inferno.customMT.Listeners;

import nl.inferno.customMT.CustomMT;
import nl.inferno.customMT.Database.SqlConnecter;
import nl.inferno.customMT.Roles.Bank.BankDuty;
import nl.inferno.customMT.Roles.BouwBedrijf.BuildCommand;
import nl.inferno.customMT.Roles.Gemeente.GemeenteDuty;
import nl.inferno.customMT.Roles.Politie.PolitieDuty;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {
    private final SqlConnecter sqlConnecter;
    private final CustomMT plugin;
    private final PolitieDuty politieDuty;
    private final GemeenteDuty gemeenteDuty;
    private final BankDuty bankDuty;
    private final BuildCommand buildCommand;

    public PlayerListener(CustomMT plugin, SqlConnecter sqlConnecter, PolitieDuty politieDuty,
                          GemeenteDuty gemeenteDuty, BankDuty bankDuty, BuildCommand buildCommand) {
        this.plugin = plugin;
        this.sqlConnecter = sqlConnecter;
        this.politieDuty = politieDuty;
        this.gemeenteDuty = gemeenteDuty;
        this.bankDuty = bankDuty;
        this.buildCommand = buildCommand;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String username = player.getName();
        String ipAddress = player.getAddress().getAddress().getHostAddress();

        event.setJoinMessage("§8[§a+§8] §7" + username);

        sqlConnecter.executeUpdate(
                "INSERT INTO players (uuid, username, ip_address, last_join) VALUES (?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE username = ?, ip_address = ?, last_join = NOW()",
                uuid, username, ipAddress, username, ipAddress
        );

        if (!player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWelkom bij InfernoMC!"));
            // First join logic here
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Remove from all duties
        if (politieDuty.isInDuty(uuid)) {
            politieDuty.removeFromDuty(player);
        }
        if (gemeenteDuty.isInDuty(uuid)) {
            gemeenteDuty.removeFromDuty(player);
        }
        if (bankDuty.isInDuty(uuid)) {
            bankDuty.removeFromDuty(player);
        }
        if (buildCommand.isInBuildMode(uuid)) {
            buildCommand.disableBuildMode(player);
        }

        event.setQuitMessage("§8[§c-§8] §7" + player.getName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("custommt.build.break") && !buildCommand.isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("custommt.build.place") && !buildCommand.isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("custommt.build.interact") && !buildCommand.isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Additional utility methods if needed
    private boolean isInAnyDuty(UUID uuid) {
        return politieDuty.isInDuty(uuid) ||
                gemeenteDuty.isInDuty(uuid) ||
                bankDuty.isInDuty(uuid) ||
                buildCommand.isInBuildMode(uuid);
    }
}
