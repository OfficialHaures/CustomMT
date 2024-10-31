package nl.inferno.customMT.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ItemPvPListener implements Listener {
    private final Set<Material> allowedPvPItems = new HashSet<>();

    public ItemPvPListener() {
        allowedPvPItems.add(Material.CHAIN);
        allowedPvPItems.add(Material.STICK);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        ItemStack item = attacker.getInventory().getItemInMainHand();

        if (!allowedPvPItems.contains(item.getType())) {
            event.setCancelled(true);
        }
    }
}
