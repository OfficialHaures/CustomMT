package nl.inferno.customMT.Listeners;

import nl.inferno.customMT.CustomMT;
import nl.inferno.customMT.Database.SqlConnecter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerListener implements Listener {

    private final SqlConnecter sqlConnecter;
    private final CustomMT plugin;
    private final FileConfiguration config;

    public PlayerListener(SqlConnecter sqlConnecter, CustomMT plugin) {
        this.sqlConnecter = sqlConnecter;
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String username = player.getName();
        String ipAdress = player.getAddress().getAddress().getHostAddress();

        event.setJoinMessage("§8[§a+§8] §7" + username);

        sqlConnecter.executeQuery("SELECT uuid FROM players WHERE uuid = ?", uuid);
        sqlConnecter.executeUpdate(
                "INSERT INTO players (uuid, username, ip_address, last_join) VALUES (?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE username = ?, ip_address = ?, last_join = NOW()",
                uuid, username, ipAdress, username, ipAdress
        );

        if(!player.hasPlayedBefore()) {
            Location spawn = new Location(
                    Bukkit.getWorld(config.getString("spawn-location.world")),
                    config.getDouble("spawn-location.x"),
                    config.getDouble("spawn-location.y"),
                    config.getDouble("spawn-location.z"),
                    (float) config.getDouble("spawn-location.yaw"),
                    (float) config.getDouble("spawn-location.pitch")
            );

            player.teleport(spawn);
            player.sendMessage("§aWelkom bij InfernoMC!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("custommt.build.break")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("custommt.build.place")) {
            event.setCancelled(true);
        }
    }
}
