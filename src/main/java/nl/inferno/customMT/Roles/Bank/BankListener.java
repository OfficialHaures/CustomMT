package nl.inferno.customMT.Roles.Bank;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankListener implements Listener {
    private final Economy economy;
    private final Map<UUID, Double> currentAmount = new HashMap<>();
    private final Map<UUID, TransactionType> transactionType = new HashMap<>();

    public BankListener(Economy economy) {
        this.economy = economy;
    }

    private enum TransactionType {
        DEPOSIT, WITHDRAW, TRANSFER
    }

    @EventHandler
    public void onBankInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.RED_SANDSTONE_STAIRS) return;

        openMainMenu(event.getPlayer());
        event.setCancelled(true);
    }

    private void openMainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§6Bank Menu");

        ItemStack balance = createMenuItem(Material.GOLD_INGOT, "§6Saldo: §a$" + economy.getBalance(player),
                "§7Je huidige saldo");
        ItemStack deposit = createMenuItem(Material.EMERALD, "§aStorten", "§7Klik om geld te storten");
        ItemStack withdraw = createMenuItem(Material.REDSTONE, "§cOpnemen", "§7Klik om geld op te nemen");
        ItemStack transfer = createMenuItem(Material.PAPER, "§eOvermaken", "§7Klik om geld over te maken");
        menu.setItem(4, balance);
        menu.setItem(11, deposit);
        menu.setItem(13, withdraw);
        menu.setItem(15, transfer);

        player.openInventory(menu);
        playClickSound(player);
    }

    private void openCalculator(Player player, TransactionType type) {
        Inventory calc = Bukkit.createInventory(null, 54, "§6Bank Calculator");
        currentAmount.put(player.getUniqueId(), 0.0);
        transactionType.put(player.getUniqueId(), type);

        for (int i = 0; i < 10; i++) {
            calc.setItem(28 + i, createMenuItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                    "§b" + i, "§7Klik om " + i + " toe te voegen"));
        }

        calc.setItem(10, createMenuItem(Material.EMERALD, "§a+$10", "§7Voeg $10 toe"));
        calc.setItem(11, createMenuItem(Material.EMERALD, "§a+$50", "§7Voeg $50 toe"));
        calc.setItem(12, createMenuItem(Material.EMERALD, "§a+$100", "§7Voeg $100 toe"));
        calc.setItem(14, createMenuItem(Material.EMERALD, "§a+$500", "§7Voeg $500 toe"));
        calc.setItem(15, createMenuItem(Material.EMERALD, "§a+$1000", "§7Voeg $1000 toe"));
        calc.setItem(16, createMenuItem(Material.EMERALD, "§a+$5000", "§7Voeg $5000 toe"));

        calc.setItem(45, createMenuItem(Material.RED_STAINED_GLASS_PANE, "§cWissen", "§7Reset bedrag"));
        calc.setItem(49, createMenuItem(Material.GREEN_STAINED_GLASS_PANE, "§aBevestigen",
                "§7Klik om transactie uit te voeren"));
        calc.setItem(53, createMenuItem(Material.BARRIER, "§cTerug", "§7Terug naar hoofdmenu"));

        updateAmountDisplay(calc, 4, 0.0);

        player.openInventory(calc);
        playClickSound(player);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;

        String title = event.getView().getTitle();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        event.setCancelled(true);

        switch (title) {
            case "§6Bank Menu" -> handleMainMenuClick(player, itemName);
            case "§6Bank Calculator" -> handleCalculatorClick(player, event.getCurrentItem(), event.getInventory());
            case "§6Overmaken" -> handleTransferClick(player, itemName);
        }
    }

    private void handleMainMenuClick(Player player, String itemName) {
        switch (itemName) {
            case "§aStorten" -> openCalculator(player, TransactionType.DEPOSIT);
            case "§cOpnemen" -> openCalculator(player, TransactionType.WITHDRAW);
            case "§eOvermaken" -> openTransferMenu(player);
        }
    }

    private void handleCalculatorClick(Player player, ItemStack clicked, Inventory inventory) {
        String itemName = clicked.getItemMeta().getDisplayName();
        double current = currentAmount.getOrDefault(player.getUniqueId(), 0.0);

        if (itemName.startsWith("§b")) {
            double num = Double.parseDouble(itemName.substring(2));
            current = (current * 10) + num;
        } else if (itemName.startsWith("§a+")) {
            String amountStr = itemName.substring(3).replace("$", "");
            double amount = Double.parseDouble(amountStr);
            current += amount;
        } else switch (itemName) {
            case "§cWissen" -> current = 0.0;
            case "§aBevestigen" -> {
                executeTransaction(player, current);
                return;
            }
            case "§cTerug" -> {
                openMainMenu(player);
                return;
            }
        }

        currentAmount.put(player.getUniqueId(), current);
        updateAmountDisplay(inventory, 4, current);
        playClickSound(player);
    }

    private void executeTransaction(Player player, double amount) {
        TransactionType type = transactionType.get(player.getUniqueId());
        boolean success = false;

        switch (type) {
            case DEPOSIT -> {
                if (economy.has(player, amount)) {
                    economy.withdrawPlayer(player, amount);
                    player.sendMessage("§aJe hebt §2$" + amount + " §agestort!");
                    success = true;
                } else {
                    player.sendMessage("§cJe hebt niet genoeg geld!");
                }
            }
            case WITHDRAW -> {
                if (economy.getBalance(player) >= amount) {
                    economy.depositPlayer(player, amount);
                    player.sendMessage("§aJe hebt §2$" + amount + " §aopgenomen!");
                    success = true;
                } else {
                    player.sendMessage("§cJe hebt niet genoeg geld op je rekening!");
                }
            }
        }

        if (success) {
            playSuccessSound(player);
            openMainMenu(player);
        } else {
            playErrorSound(player);
        }
    }

    private void updateAmountDisplay(Inventory inventory, int slot, double amount) {
        inventory.setItem(slot, createMenuItem(Material.GOLD_INGOT,
                "§6Bedrag: §a$" + amount, "§7Huidig geselecteerd bedrag"));
    }

    private void openTransferMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 36, "§6Overmaken");

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target != player) {
                menu.addItem(createMenuItem(Material.PLAYER_HEAD,
                        "§e" + target.getName(), "§7Klik om geld over te maken"));
            }
        }

        player.openInventory(menu);
        playClickSound(player);
    }

    private void handleTransferClick(Player player, String itemName) {
        if (itemName.startsWith("§e")) {
            String targetName = itemName.substring(2);
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                transactionType.put(player.getUniqueId(), TransactionType.TRANSFER);
                openCalculator(player, TransactionType.TRANSFER);
            }
        }
    }

    private void playClickSound(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }

    private void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    private void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
