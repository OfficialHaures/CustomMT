package nl.inferno.customMT.Roles.Gemeente;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GemeentePlotManager {

    public static void openPlotMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6Plot Beheer");

        // Plot creation
        ItemStack createPlot = createMenuItem(Material.EMERALD, "§aPlot Aanmaken",
                "§7Klik om een nieuw plot aan te maken");

        // Plot deletion
        ItemStack deletePlot = createMenuItem(Material.REDSTONE, "§cPlot Verwijderen",
                "§7Klik om dit plot te verwijderen");

        // Plot info
        ItemStack plotInfo = createMenuItem(Material.BOOK, "§ePlot Informatie",
                "§7Bekijk plot details");

        // Plot list
        ItemStack plotList = createMenuItem(Material.MAP, "§6Alle Plots",
                "§7Bekijk alle plots");

        gui.setItem(10, createPlot);
        gui.setItem(12, deletePlot);
        gui.setItem(14, plotInfo);
        gui.setItem(16, plotList);

        player.openInventory(gui);
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
