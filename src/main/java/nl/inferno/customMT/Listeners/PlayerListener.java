
package nl.inferno.customMT.Listeners;

import nl.inferno.customMT.Database.SqlConnecter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    private SqlConnecter sqlConnecter;

    public PlayerListener(SqlConnecter sqlConnecter) {
        this.sqlConnecter = sqlConnecter;
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
        if(!player.hasPlayedBefore()){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Welkom bij InfernoMC!"));
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("custommt.build.interact")) {
            event.setCancelled(true);
        }
    }
}

