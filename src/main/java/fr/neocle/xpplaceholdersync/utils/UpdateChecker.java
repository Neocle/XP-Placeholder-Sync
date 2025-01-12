package fr.neocle.xpplaceholdersync.utils;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getLatestVersion(VersionCallback callback) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                @SuppressWarnings("deprecation")
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                String latestVersion = response.get("version").getAsString();
                callback.onComplete(latestVersion);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    public interface VersionCallback {
        void onComplete(String version);
    }
}
