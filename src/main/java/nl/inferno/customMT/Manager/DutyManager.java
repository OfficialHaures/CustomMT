package nl.inferno.customMT.Manager;

import nl.inferno.customMT.Database.SqlConnecter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DutyManager {
    private final SqlConnecter sql;
    private final String tableName;

    public DutyManager(SqlConnecter sql, String tableName) {
        this.sql = sql;
        this.tableName = tableName;
        createTable();
    }

    private void createTable() {
        sql.executeUpdate(
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "inventory TEXT NOT NULL," +
                        "armor TEXT NOT NULL" +
                        ")"
        );
    }

    public void saveInventory(Player player) {
        String inventoryBase64 = toBase64(player.getInventory().getContents());
        String armorBase64 = toBase64(player.getInventory().getArmorContents());

        sql.executeUpdate(
                "INSERT INTO " + tableName + " (uuid, inventory, armor) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE inventory = ?, armor = ?",
                player.getUniqueId().toString(), inventoryBase64, armorBase64, inventoryBase64, armorBase64
        );
    }

    public void restoreInventory(Player player) {
        var result = sql.executeQuery(
                "SELECT inventory, armor FROM " + tableName + " WHERE uuid = ?",
                player.getUniqueId().toString()
        );

        try {
            if (result.next()) {
                String inventoryBase64 = result.getString("inventory");
                String armorBase64 = result.getString("armor");

                player.getInventory().setContents(fromBase64(inventoryBase64));
                player.getInventory().setArmorContents(fromBase64(armorBase64));

                sql.executeUpdate("DELETE FROM " + tableName + " WHERE uuid = ?",
                        player.getUniqueId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
