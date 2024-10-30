package nl.inferno.customMT.Roles.Politie;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PolitieListener implements Listener {
    private final PolitieDuty politieDuty;
    private final Map<UUID, Long> tazerCooldown = new HashMap<>();
    private final Map<UUID, Long> tazedPlayers = new HashMap<>();

    public PolitieListener(PolitieDuty politieDuty) {
        this.politieDuty = politieDuty;
    }

    @EventHandler
    public void onTazer(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!player.hasPermission("custommt.police.tazer")) return;
        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().getDisplayName().equals("§eTazer")) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Check cooldown
        if (tazerCooldown.containsKey(player.getUniqueId())) {
            long timeLeft = System.currentTimeMillis() - tazerCooldown.get(player.getUniqueId());
            if (timeLeft < 5000) {
                player.sendMessage("§cJe moet nog " + ((5000 - timeLeft) / 1000) + " seconden wachten!");
                return;
            }
        }

        // Get target and shoot tazer
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection();

        new BukkitRunnable() {
            Location loc = eyeLoc.clone();
            int distance = 0;

            @Override
            public void run() {
                distance++;

                // Move particle forward
                loc.add(direction.clone().multiply(1));

                // Particle effects
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
                        new Particle.DustOptions(Color.YELLOW, 1));
                loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 2, 0.1, 0.1, 0.1, 0);

                // Check for hit
                for (Player target : loc.getWorld().getPlayers()) {
                    if (target == player) continue;
                    if (loc.distance(target.getLocation().add(0, 1, 0)) < 1.5) {
                        hitPlayer(target);
                        this.cancel();
                        return;
                    }
                }

                // Check max distance or solid block
                if (distance > 20 || loc.getBlock().getType().isSolid()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(politieDuty.getPlugin(), 0L, 1L);

        // Set cooldown and play sound
        tazerCooldown.put(player.getUniqueId(), System.currentTimeMillis());
        player.playSound(player.getLocation(), Sound.ENTITY_BEE_HURT, 1.0f, 2.0f);
    }

    private void hitPlayer(Player target) {
        // Visual and sound effects
        target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                target.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);

        // Apply effects
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 7));
        target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 128));
        target.setVelocity(new Vector(0, 0, 0));

        // Track tazed state
        tazedPlayers.put(target.getUniqueId(), System.currentTimeMillis());
        target.sendMessage("§cJe bent getazerd!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!tazedPlayers.containsKey(player.getUniqueId())) return;

        long timeLeft = System.currentTimeMillis() - tazedPlayers.get(player.getUniqueId());
        if (timeLeft < 5000) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                event.setCancelled(true);
            }
        } else {
            tazedPlayers.remove(player.getUniqueId());
        }
    }
    @EventHandler
    public void onHandcuffLeftClick(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof Player target)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!politieDuty.isInDuty(player.getUniqueId())) return;
        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().getDisplayName().equals("§7Handboeien")) return;

        event.setCancelled(true);

        if (!politieDuty.isHandcuffed(target.getUniqueId())) {
            politieDuty.setCuffer(target.getUniqueId(), player.getUniqueId());
            politieDuty.toggleHandcuffs(target.getUniqueId());
            target.sendMessage("§cJe bent geboeid!");
            player.sendMessage("§aJe hebt " + target.getName() + " geboeid!");
            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onHandcuffRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!politieDuty.isInDuty(player.getUniqueId())) return;
        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().getDisplayName().equals("§7Handboeien")) return;

        if (politieDuty.isHandcuffed(target.getUniqueId()) &&
                politieDuty.getCuffer(target.getUniqueId()).equals(player.getUniqueId())) {
            politieDuty.toggleHandcuffs(target.getUniqueId());
            target.sendMessage("§aJe bent niet meer geboeid!");
            player.sendMessage("§aJe hebt " + target.getName() + " ontboeid!");
            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1.0f, 1.0f);
        }
    }



    @EventHandler
    public void onHandcuffedPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!politieDuty.isHandcuffed(player.getUniqueId())) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHandcuffedPlayerInteract(PlayerInteractEvent event) {
        if (politieDuty.isHandcuffed(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHandcuffedInventoryClick(InventoryClickEvent event) {
        if (politieDuty.isHandcuffed(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
