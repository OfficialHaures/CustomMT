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
    private Economy economy;
    private Map<UUID, UUID> transferTarget = new HashMap<>();

    public BankListener(Economy economy) {
        this.economy = economy;
    }

    @EventHandler
    public void onBankInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() == Material.RED_SANDSTONE_STAIRS) {
            openBankMenu(event.getPlayer());
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        String title = event.getView().getTitle();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch (title) {
            case "§6Bank Menu" -> handleMainMenu(player, itemName);
            case "§aStorten" -> handleDeposit(player, itemName);
            case "§cOpnemen" -> handleWithdraw(player, itemName);
            case "§eOverboeken" -> handleTransfer(player, itemName);
        }

        event.setCancelled(true);
    }

    private void handleMainMenu(Player player, String itemName) {
        switch (itemName) {
            case "§aGeld Storten" -> openDepositMenu(player);
            case "§cGeld Opnemen" -> openWithDrawMenu(player);
            case "§eOverboeken" -> openTransferMenu(player);
            case "§6Saldo Bekijken" -> showBalance(player);
        }
    }

    private void handleDeposit(Player player, String itemName) {
        if (itemName.startsWith("§a$")) {
            double amount = Double.parseDouble(itemName.substring(3));
            if (economy.has(player, amount)) {
                player.sendMessage("§aJe hebt §2$" + amount + " §agestort!");
                playSuccessSound(player);
            } else {
                player.sendMessage("§cJe hebt niet genoeg geld!");
                playErrorSound(player);
            }
            player.closeInventory();
        }
    }

    private void handleWithdraw(Player player, String itemName) {
        if (itemName.startsWith("§c$")) {
            double amount = Double.parseDouble(itemName.substring(3));
            if (economy.getBalance(player) >= amount) {
                economy.depositPlayer(player, amount);
                player.sendMessage("§cJe hebt §2$" + amount + " §cgehaald!");
                playSuccessSound(player);
            } else {
                player.sendMessage("§cJe hebt niet genoeg geld!");
                playErrorSound(player);
            }
            player.closeInventory();
        }
    }

    private void handleTransfer(Player player, String itemName) {
        if (itemName.startsWith("§e")) {
            String targetName = itemName.substring(2);
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                transferTarget.put(player.getUniqueId(), target.getUniqueId());
                openTransferAmountMenu(player);
            }
        } else if (itemName.startsWith("§a$")) {
            UUID targetUUID = transferTarget.get(player.getUniqueId());
            if (targetUUID != null) {
                Player target = Bukkit.getPlayer(targetUUID);
                if (target != null) {
                    double amount = Double.parseDouble(itemName.substring(3));
                    if (economy.getBalance(player) >= amount) {
                        economy.withdrawPlayer(player, amount);
                        economy.depositPlayer(target, amount);
                        player.sendMessage("§aJe hebt §2$" + amount + " §agetransferd naar §e" + target.getName() + "!");
                        target.sendMessage("§aJe hebt §2$" + amount + " §agestort van §e" + player.getName() + "!");
                        playSuccessSound(player);
                        playSuccessSound(target);
                    } else {
                        player.sendMessage("§cJe hebt niet genoeg geld!");
                        playErrorSound(player);
                    }
                }
            }
            player.closeInventory();
            transferTarget.remove(player.getUniqueId());
        }
    }


    private void openBankMenu(Player player) {

        Inventory menu = Bukkit.createInventory(null, 27, "§6Bank Menu");

        ItemStack deposit = createMenuItem(Material.EMERALD, "§aGeld Storten", "§7Klik om geld te storten");
        ItemStack withdraw = createMenuItem(Material.REDSTONE, "§cGeld Opnemen", "§7Klik om geld op te nemen.");
        ItemStack transfer = createMenuItem(Material.ENDER_PEARL, "§eGeld Overboeken", "§7Klik om geld over te maken.");
        ItemStack balance = createMenuItem(Material.GOLD_INGOT, "§6Saldo Bekijken",
                "§7Huidig saldo: §a$" + economy.getBalance(player));

        menu.setItem(10, deposit);
        menu.setItem(12, withdraw);
        menu.setItem(14, transfer);
        menu.setItem(16, balance);

        player.openInventory(menu);
    }

    private void openDepositMenu(Player player){
        Inventory menu = Bukkit.createInventory(null, 27, "§aStorten");

        menu.setItem(10, createMenuItem(Material.EMERALD, "§a$10", "§7Klik om te storten"));
        menu.setItem(12, createMenuItem(Material.EMERALD, "§a$100", "§7Klik om te storten"));
        menu.setItem(14, createMenuItem(Material.EMERALD, "§a$1000", "§7Klik om te storten"));
        menu.setItem(16, createMenuItem(Material.EMERALD, "§a$10000", "§7Klik om te storten"));

        player.openInventory(menu);
    }

    private void openWithDrawMenu(Player player){
        Inventory menu = Bukkit.createInventory(null, 27, "§cOpnemen");

        menu.setItem(10, createMenuItem(Material.REDSTONE, "§c$10", "§7Klik om op te nemen"));
        menu.setItem(12, createMenuItem(Material.REDSTONE, "§c$100", "§7Klik om op te nemen"));
        menu.setItem(14, createMenuItem(Material.REDSTONE, "§c$1000", "§7Klik om op te nemen"));
        menu.setItem(16, createMenuItem(Material.REDSTONE, "§c$10000", "§7Klik om op te nemen"));

        player.openInventory(menu);
    }

    private void openTransferMenu(Player player){
        Inventory menu = Bukkit.createInventory( null, 36, "§eOverboeken");
        int slot = 0;

        for(Player target : Bukkit.getOnlinePlayers()){
            if(target != player){
                menu.setItem(slot, createMenuItem(Material.PLAYER_HEAD, "§e" + target.getName(), "§7Klik om geld over te maken"));
                slot++;
            }
        }
        player.openInventory(menu);
    }

    private void openTransferAmountMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§eOverboeken");
    menu.setItem(10, createMenuItem(Material.EMERALD, "§a$10", "§7Klik om over te maken"));
    menu.setItem(12, createMenuItem(Material.EMERALD, "§a$100", "§7Klik om over te maken"));
    menu.setItem(14, createMenuItem(Material.EMERALD, "§a$1000", "§7Klik om over te maken"));
    menu.setItem(16, createMenuItem(Material.EMERALD, "§a$10000", "§7Klik om over te maken"));

    player.openInventory(menu);
    }

    private void showBalance(Player player){
        double balance = economy.getBalance(player);
        player.sendMessage("§6Je saldo: §a$" + balance);
        playSuccessSound(player);
    }
    private void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }
    private void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
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
