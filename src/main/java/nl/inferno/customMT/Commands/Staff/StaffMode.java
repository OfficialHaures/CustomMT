package nl.inferno.customMT.Commands.Staff;

import nl.inferno.customMT.Database.SqlConnecter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StaffMode implements CommandExecutor {
    private final Set<UUID> inStaffMode = new HashSet<>();
    private final SqlConnecter sqlConnecter;

    public StaffMode(SqlConnecter sqlConnecter) {
        this.sqlConnecter = sqlConnecter;
        createTable();
    }

    private void createTable() {
        sqlConnecter.executeUpdate(
                "CREATE TABLE IF NOT EXISTS staff_inventories (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "inventory TEXT NOT NULL," +
                        "armor TEXT NOT NULL" +
                        ")"
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("custommt.staff")) {
            player.sendMessage("§cJe hebt geen toegang tot dit command!");
            return true;
        }

        if (inStaffMode.contains(player.getUniqueId())) {
            disableStaffMode(player);
        } else {
            enableStaffMode(player);
        }

        return true;
    }

    private void enableStaffMode(Player player) {
        inStaffMode.add(player.getUniqueId());

        // Save current inventory
        String inventoryBase64 = toBase64(player.getInventory().getContents());
        String armorBase64 = toBase64(player.getInventory().getArmorContents());

        sqlConnecter.executeUpdate(
                "INSERT INTO staff_inventories (uuid, inventory, armor) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE inventory = ?, armor = ?",
                player.getUniqueId().toString(), inventoryBase64, armorBase64, inventoryBase64, armorBase64
        );

        // Clear and set staff items
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);

        giveStaffItems(player);

        player.sendMessage("§aStaffmode ingeschakeld!");
    }

    private void disableStaffMode(Player player) {
        inStaffMode.remove(player.getUniqueId());
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);

        // Restore inventory from database
        var result = sqlConnecter.executeQuery(
                "SELECT inventory, armor FROM staff_inventories WHERE uuid = ?",
                player.getUniqueId().toString()
        );

        try {
            if (result.next()) {
                String inventoryBase64 = result.getString("inventory");
                String armorBase64 = result.getString("armor");

                // Restore inventories
                player.getInventory().setContents(fromBase64(inventoryBase64));
                player.getInventory().setArmorContents(fromBase64(armorBase64));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.sendMessage("§cStaffmode uitgeschakeld!");
    }

    private void giveStaffItems(Player player) {
        ItemStack vanish = createItem(Material.LIME_DYE, "§aVanish");
        ItemStack inspect = createItem(Material.BOOK, "§eSpeler Inspecteren");
        ItemStack freeze = createItem(Material.ICE, "§bSpeler Bevriezen");
        ItemStack randomTp = createItem(Material.ENDER_PEARL, "§5Random Teleport");

        player.getInventory().setItem(0, vanish);
        player.getInventory().setItem(1, inspect);
        player.getInventory().setItem(2, freeze);
        player.getInventory().setItem(4, randomTp);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private String toBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private ItemStack[] fromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }

    public boolean isInStaffMode(UUID uuid) {
        return inStaffMode.contains(uuid);
    }
}
