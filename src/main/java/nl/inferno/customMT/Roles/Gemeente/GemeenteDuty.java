package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GemeenteDuty implements CommandExecutor {
    private final Set<UUID> inDuty = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDit command kan alleen door spelers worden gebruikt!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("custommt.gemeente")) {
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

    private void addToDuty(Player player) {
        inDuty.add(player.getUniqueId());

        // Clear inventory
        player.getInventory().clear();

        // Give gemeente armor
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta;
        for (ItemStack item : new ItemStack[]{helmet, chestplate, leggings, boots}) {
            meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(255, 140, 0)); // Orange color
            meta.setDisplayName("§6Gemeente Uniform");
            item.setItemMeta(meta);
        }

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        // Give gemeente tools
        ItemStack plotManager = createCustomItem(
                Material.BOOK,
                "§6Plot Manager",
                "§7Rechtsklik om plots te beheren",
                "§7- Voeg eigenaren toe",
                "§7- Verwijder eigenaren",
                "§7- Beheer plot rechten"
        );

        ItemStack plotWand = createCustomItem(
                Material.GOLDEN_AXE,
                "§6Plot Selector",
                "§7Linkerclick: Selecteer punt 1",
                "§7Rechterclick: Selecteer punt 2"
        );

        ItemStack plotInfo = createCustomItem(
                Material.PAPER,
                "§6Plot Informatie",
                "§7Rechtsklik om plot info te zien"
        );

        ItemStack plotCreate = createCustomItem(
                Material.EMERALD,
                "§6Plot Aanmaken",
                "§7Rechtsklik om een plot aan te maken"
        );

        // Add items to inventory
        player.getInventory().setItem(0, plotManager);
        player.getInventory().setItem(1, plotWand);
        player.getInventory().setItem(2, plotInfo);
        player.getInventory().setItem(3, plotCreate);

        player.sendMessage("§aJe bent nu in dienst bij de gemeente!");
    }

    private void removeFromDuty(Player player) {
        inDuty.remove(player.getUniqueId());
        player.getInventory().clear();
        player.sendMessage("§cJe bent nu uit dienst!");
    }

    private ItemStack createCustomItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isInDuty(UUID uuid) {
        return inDuty.contains(uuid);
    }

    public void openPlotManager(Player player) {
        GemeenteGui.openPlotManager(player);
    }

    public void openAdministrationMenu(Player player) {
        GemeenteGui.openAdministrationMenu(player);
    }
}
