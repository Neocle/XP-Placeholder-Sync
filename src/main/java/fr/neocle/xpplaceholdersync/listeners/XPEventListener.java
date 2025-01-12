package fr.neocle.xpplaceholdersync.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.neocle.xpplaceholdersync.XPPlaceholderSync;

public class XPEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerXPBar(player);
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    private void updatePlayerXPBar(Player player) {
        String placeholderValue = PlaceholderAPI.setPlaceholders(player, "%" + XPPlaceholderSync.getInstance().placeholder + "%");

        double xpValue;
        try {
            xpValue = Double.parseDouble(placeholderValue.replace(',', '.'));
        } catch (NumberFormatException e) {
            xpValue = 0.0;
        }

        int level = (int) Math.floor(xpValue);
        float progress = (float) (xpValue - level);

        player.setLevel(level);
        player.setExp(progress);
    }
}
