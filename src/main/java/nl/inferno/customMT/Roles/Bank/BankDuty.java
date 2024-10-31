package nl.inferno.customMT.Roles.Bank;

import nl.inferno.customMT.CustomMT;
import nl.inferno.customMT.Manager.DutyManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BankDuty implements CommandExecutor {
    private final Set<UUID> inDuty = new HashSet<>();
    private final DutyManager dutyManager;
    private final CustomMT plugin;

    public BankDuty(CustomMT plugin) {
        this.plugin = plugin;
        this.dutyManager = new DutyManager(plugin.getSql(), "bank_duty");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDit command kan alleen door spelers worden gebruikt!");
            return true;
        }

        if (!player.hasPermission("custommt.bank")) {
            player.sendMessage("§cJe hebt geen toegang tot dit command!");
            return true;
        }

        if (inDuty.contains(player.getUniqueId())) {
            removeFromDuty(player);
        } else {
            addToDuty(player);
        }

        return true;
    }

    public void addToDuty(Player player) {
        inDuty.add(player.getUniqueId());
        dutyManager.saveInventory(player);
        player.getInventory().clear();

        ItemStack uniform = createCustomItem(Material.LEATHER_CHESTPLATE, "§6Bank Uniform");
        ItemStack bankBook = createCustomItem(Material.BOOK, "§6Bank Administratie");

        player.getInventory().setChestplate(uniform);
        player.getInventory().addItem(bankBook);
        player.sendMessage("§aJe bent nu in dienst bij de bank!");
    }

    public void removeFromDuty(Player player) {
        inDuty.remove(player.getUniqueId());
        player.getInventory().clear();
        dutyManager.restoreInventory(player);
        player.sendMessage("§cJe bent nu uit dienst!");
    }

    private ItemStack createCustomItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isInDuty(UUID uuid) {
        return inDuty.contains(uuid);
    }

    public CustomMT getPlugin() {
        return plugin;
    }
}
