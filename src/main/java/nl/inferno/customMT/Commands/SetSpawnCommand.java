package nl.inferno.customMT.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SetSpawnCommand implements CommandExecutor {
    private final Plugin plugin;
    private final FileConfiguration config;

    public SetSpawnCommand(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDit command kan alleen door spelers worden gebruikt!");
            return true;
        }

        if (!player.hasPermission("custommt.setspawn")) {
            player.sendMessage("§cJe hebt geen toegang tot dit command!");
            return true;
        }

        Location location = player.getLocation();

        config.set("spawn-location.world", location.getWorld().getName());
        config.set("spawn-location.x", location.getX());
        config.set("spawn-location.y", location.getY());
        config.set("spawn-location.z", location.getZ());
        config.set("spawn-location.yaw", location.getYaw());
        config.set("spawn-location.pitch", location.getPitch());

        plugin.saveConfig();

        player.sendMessage("§aSpawn locatie is succesvol ingesteld!");
        return true;
    }
}
