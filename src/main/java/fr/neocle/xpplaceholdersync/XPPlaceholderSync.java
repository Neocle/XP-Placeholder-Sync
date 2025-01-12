package fr.neocle.xpplaceholdersync;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.neocle.xpplaceholdersync.listeners.XPEventListener;
import fr.neocle.xpplaceholdersync.utils.UpdateChecker;
import fr.neocle.xpplaceholdersync.utils.XPUpdateTask;

public class XPPlaceholderSync extends JavaPlugin {

    private static XPPlaceholderSync instance;
    public String placeholder;
    private int updateRate;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        instance = this;

        getServer().getPluginManager().registerEvents(new XPEventListener(), this);

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().severe("PlaceholderAPI is not installed or enabled!");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            getLogger().info("PlaceholderAPI found and hooked.");
        }

        int pluginId = 24426;
        new Metrics(this, pluginId);

        startUpdateTask();

        checkForUpdates();
    }

    private void loadConfigValues() {
        placeholder = getConfig().getString("placeholder", "yourplaceholder");
        updateRate = getConfig().getInt("update-rate", 5);
    }

    private void startUpdateTask() {
        new XPUpdateTask(this).runTaskTimer(this, 0, updateRate * 20L);
    }

    private void checkForUpdates() {
        new UpdateChecker(this, 121893).getLatestVersion(version -> {
            if (!getDescription().getVersion().equals(version)) {
                getLogger().warning("There is a new version available: " + version);
                getLogger().warning("Download it at: https://www.spigotmc.org/resources/xp-placeholder-sync.121893/");
            } else {
                getLogger().info("The plugin is up to date.");
            }
        });
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

    public static XPPlaceholderSync getInstance() {
        return instance;
    }

}
