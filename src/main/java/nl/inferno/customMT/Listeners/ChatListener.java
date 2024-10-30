package nl.inferno.customMT.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private static final double CHAT_RANGE = 10.0;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player sender = event.getPlayer();
        String message = event.getMessage();

        String formattedMessage = "§7" + sender.getName() + ": §f" + message;

        for (Player nearbyPlayer : sender.getWorld().getPlayers()) {
            if (sender.getLocation().distance(nearbyPlayer.getLocation()) <= CHAT_RANGE) {
                nearbyPlayer.sendMessage(formattedMessage);
            }
        }
    }
}
