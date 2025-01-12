package fr.neocle.xpplaceholdersync.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    @SuppressWarnings("deprecation")
    public void getLatestVersion(VersionCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {

                if (scanner.hasNext()) {
                    String latestVersion = scanner.next();
                    callback.onComplete(latestVersion);
                } else {
                    plugin.getLogger().warning("No version information found for resource " + resourceId);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    public interface VersionCallback {
        void onComplete(String version);
    }
}
