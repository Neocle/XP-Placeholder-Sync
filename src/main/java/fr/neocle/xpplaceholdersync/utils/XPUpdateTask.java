package fr.neocle.xpplaceholdersync.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.neocle.xpplaceholdersync.XPPlaceholderSync;
import me.clip.placeholderapi.PlaceholderAPI;

public class XPUpdateTask extends BukkitRunnable {

    private final XPPlaceholderSync plugin;

    public XPUpdateTask(XPPlaceholderSync plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String placeholderValue = PlaceholderAPI.setPlaceholders(player, "%" + plugin.placeholder + "%");
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
}
