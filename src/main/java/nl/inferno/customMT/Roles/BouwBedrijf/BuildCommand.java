package nl.inferno.customMT.Roles.BouwBedrijf;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BuildCommand implements CommandExecutor {
    private final Set<UUID> inBuildMode = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDit command kan alleen door spelers worden gebruikt!");
            return true;
        }

        if (!player.hasPermission("custommt.build")) {
            player.sendMessage("§cJe hebt geen toegang tot dit command!");
            return true;
        }

        if (inBuildMode.contains(player.getUniqueId())) {
            disableBuildMode(player);
        } else {
            enableBuildMode(player);
        }

        return true;
    }

    private void enableBuildMode(Player player) {
        inBuildMode.add(player.getUniqueId());
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);

        ItemStack worldEdit = createBuildItem(Material.WOODEN_AXE, "§6WorldEdit Wand",
                "§7Selecteer gebieden om te bewerken");

        ItemStack copy = createBuildItem(Material.COMPASS, "§aCopy Tool",
                "§7Kopieer constructies");

        ItemStack paste = createBuildItem(Material.BLAZE_ROD, "§ePaste Tool",
                "§7Plak gekopieerde constructies");

        ItemStack undo = createBuildItem(Material.BARRIER, "§cUndo",
                "§7Maak laatste actie ongedaan");

        player.getInventory().setItem(0, worldEdit);
        player.getInventory().setItem(1, copy);
        player.getInventory().setItem(2, paste);
        player.getInventory().setItem(8, undo);

        // WorldGuard override
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldguard.bukkit.WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
        com.sk89q.worldguard.LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
        container.createQuery().testState(BukkitAdapter.adapt(player.getLocation()), localPlayer, Flags.BUILD);

        player.sendMessage("§aBouwmodus ingeschakeld!");
    }

    private void disableBuildMode(Player player) {
        inBuildMode.remove(player.getUniqueId());
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage("§cBouwmodus uitgeschakeld!");
    }

    private ItemStack createBuildItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isInBuildMode(UUID uuid) {
        return inBuildMode.contains(uuid);
    }
}
