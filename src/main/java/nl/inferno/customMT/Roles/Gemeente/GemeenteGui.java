package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GemeenteGui {

    public static void openPlotManager(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6Plot Manager");

        ItemStack addOwner = createGuiItem(Material.PLAYER_HEAD, "§aVoeg Eigenaar Toe",
                "§7Klik om een speler toe te voegen");
        ItemStack removeOwner = createGuiItem(Material.BARRIER, "§cVerwijder Eigenaar",
                "§7Klik om de eigenaar te verwijderen");
        ItemStack addMember = createGuiItem(Material.PAPER, "§aVoeg Lid Toe",
                "§7Klik om een lid toe te voegen");
        ItemStack info = createGuiItem(Material.BOOK, "§6Plot Informatie",
                "§7Bekijk plot details");

        gui.setItem(11, addOwner);
        gui.setItem(13, removeOwner);
        gui.setItem(15, addMember);
        gui.setItem(22, info);

        player.openInventory(gui);
    }

    public static void openAdministrationMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "§6Gemeente Administratie");

        ItemStack newPlot = createGuiItem(Material.MAP, "§aNieuw Plot",
                "§7Maak een nieuw plot aan");
        ItemStack deletePlot = createGuiItem(Material.REDSTONE, "§cVerwijder Plot",
                "§7Verwijder een bestaand plot");
        ItemStack plotList = createGuiItem(Material.BOOK, "§6Plot Lijst",
                "§7Bekijk alle plots");
        ItemStack settings = createGuiItem(Material.COMPARATOR, "§eInstellingen",
                "§7Beheer plot instellingen");

        gui.setItem(11, newPlot);
        gui.setItem(13, deletePlot);
        gui.setItem(15, plotList);
        gui.setItem(31, settings);

        player.openInventory(gui);
    }

    private static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
