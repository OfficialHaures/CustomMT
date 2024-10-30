package nl.inferno.customMT.Roles.Politie;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;

public class PolitieBookGUI {

    public static void openBookMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§9Politie Boek");

        ItemStack writeFine = createMenuItem(Material.PAPER, "§cBoete Schrijven",
                "§7Schrijf een boete uit voor een speler");

        ItemStack writeLicense = createMenuItem(Material.MAP, "§aVergunning Schrijven",
                "§7Maak een nieuwe vergunning aan");

        ItemStack writeReport = createMenuItem(Material.BOOK, "§eRapport Schrijven",
                "§7Schrijf een politie rapport");

        ItemStack viewHistory = createMenuItem(Material.BOOKSHELF, "§6Bekijk Geschiedenis",
                "§7Bekijk alle geschreven documenten");

        gui.setItem(10, writeFine);
        gui.setItem(12, writeLicense);
        gui.setItem(14, writeReport);
        gui.setItem(16, viewHistory);

        player.openInventory(gui);
    }

    public static void openWriteBook(Player player, String type) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        switch(type) {
            case "FINE" -> {
                meta.setTitle("§cBoete");
                meta.setAuthor(player.getName());
                meta.addPage("Naam overtreder:\nDatum:\nReden:\nBedrag:\n\nHandtekening:");
            }
            case "LICENSE" -> {
                meta.setTitle("§aVergunning");
                meta.setAuthor(player.getName());
                meta.addPage("Vergunning voor:\nType:\nGeldig tot:\n\nHandtekening:");
            }
            case "REPORT" -> {
                meta.setTitle("§eRapport");
                meta.setAuthor(player.getName());
                meta.addPage("Datum:\nLocatie:\nBetrokkenen:\n\nBeschrijving:");
            }
        }

        book.setItemMeta(meta);
        player.openBook(book);
    }

    private static ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
