package nl.inferno.customMT.Roles.BouwBedrijf;

import nl.inferno.customMT.CustomMT;
import nl.inferno.customMT.Manager.DutyManager;
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
    private final DutyManager dutyManager;
    private final CustomMT plugin;

    public BuildCommand(CustomMT plugin) {
        this.plugin = plugin;
        this.dutyManager = new DutyManager(plugin.getSql(), "build_duty");
    }

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

    public void enableBuildMode(Player player) {
        inBuildMode.add(player.getUniqueId());
        dutyManager.saveInventory(player);
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

        player.sendMessage("§aBouwmodus ingeschakeld!");
    }

    public void disableBuildMode(Player player) {
        inBuildMode.remove(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        dutyManager.restoreInventory(player);
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

    public CustomMT getPlugin() {
        return plugin;
    }
}
