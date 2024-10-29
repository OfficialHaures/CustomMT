package nl.inferno.customMT.Roles.Politie;

import nl.inferno.customMT.CustomMT;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PolitieDuty implements CommandExecutor {
    private final Set<UUID> inDuty = new HashSet<>();
    private final Set<UUID> handcuffedPlayers = new HashSet<>();
    private final CustomMT plugin;

    public PolitieDuty(CustomMT plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("custommt.police")) {
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
        player.getInventory().clear();

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta;
        for (ItemStack item : new ItemStack[]{helmet, chestplate, leggings, boots}) {
            meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.NAVY);
            item.setItemMeta(meta);
        }

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        ItemStack gun = createCustomItem(Material.BOW, "§bPolitie Pistool");
        ItemStack ammo = new ItemStack(Material.ARROW, 64);
        ItemStack tazer = createCustomItem(Material.STICK, "§eTazer");
        ItemStack handcuffs = createCustomItem(Material.CHAIN, "§7Handboeien");

        player.getInventory().addItem(gun, ammo, tazer, handcuffs);

        player.sendMessage("§aJe bent nu in dienst!");
    }

    private void removeFromDuty(Player player) {
        inDuty.remove(player.getUniqueId());
        player.getInventory().clear();
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

    public boolean isHandcuffed(UUID uuid) {
        return handcuffedPlayers.contains(uuid);
    }

    public void toggleHandcuffs(UUID uuid) {
        if (handcuffedPlayers.contains(uuid)) {
            handcuffedPlayers.remove(uuid);
        } else {
            handcuffedPlayers.add(uuid);
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
