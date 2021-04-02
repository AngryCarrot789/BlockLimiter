package reghzy.blocklimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.REghZyBasePlugin;
import reghzy.blocklimiter.logs.ChatFormat;
import reghzy.blocklimiter.logs.ChatLogger;

import java.io.File;
import java.io.InputStream;

public class Config extends YamlConfiguration {
    private final File file;
    private final JavaPlugin plugin;
    private final ChatLogger logger;

    public static Config getConfigInPlugin(REghZyBasePlugin plugin, String name) {
        return new Config(plugin, FileHelper.getFileInDataFolder((Plugin) plugin, name));
    }

    public static Config getConfigInFolderInPlugin(REghZyBasePlugin plugin, String folder, String name) {
        return new Config(plugin, new File(FileHelper.getFileInDataFolder((Plugin) plugin, folder), name));
    }

    public Config(REghZyBasePlugin plugin, File configFile) {
        this.plugin = (JavaPlugin) plugin;
        this.logger = plugin.getChatLogger();
        this.file = configFile;
    }

    public boolean createNewFileOrGetDefault() {
        return createFromDefaultFile() || createNewFile();

    }

    public boolean fileExists() {
        return this.file.exists();
    }

    /**
     * Returns true if the file exists in the config file's location, or if a
     * default config file was found and copied to the config file's location successfully
     */
    public boolean createFromDefaultFile() {
        if (this.file.exists()) {
            return true;
        }

        InputStream defaultConfig = FileHelper.getDefaultConfig(this.plugin, this.file.getName());
        if (defaultConfig == null)
            return false;

        if (FileHelper.copyResourceTo(defaultConfig, this.file)) {
            logger.logPlugin(ChatFormat.red("Copied default config to " + this.file.getAbsolutePath()));
            return tryLoadYaml();
        }

        return false;
    }

    public boolean createNewFile() {
        if (this.file.exists())
            return true;

        try {
            return this.file.createNewFile();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tryLoadYaml() {
        if (file.exists()) {
            try {
                this.map.clear();
                load(this.file);
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean trySaveYaml() {
        try {
            if (this.file.exists()) {
                super.save(this.file);
                return true;
            }

            return false;
        }
        catch (Exception e) {
            return false;
        }
    }
}