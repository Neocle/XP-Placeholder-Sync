package fr.neocle.xpplaceholdersync;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class XPPlaceholderSync extends JavaPlugin implements Listener {

    private String placeholder;
    private int updateRate;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        getServer().getPluginManager().registerEvents(this, this);

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().severe("PlaceholderAPI is not installed or enabled!");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            getLogger().info("PlaceholderAPI found and hooked.");
        }

        startUpdateTask();
    }

    private void loadConfigValues() {
        placeholder = getConfig().getString("placeholder", "yourplaceholder");
        updateRate = getConfig().getInt("update-rate", 5);
    }

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
        String placeholderValue = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");

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

    private void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updatePlayerXPBar(player);
                }
            }
        }.runTaskTimer(this, 0, updateRate * 20L);
    }
    

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("xps-reload")) {
            if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
                reloadConfig();
                loadConfigValues();
                sender.sendMessage("XPPlaceholderSync configuration reloaded.");
                return true;
            }
        }
        return false;
    }
}
