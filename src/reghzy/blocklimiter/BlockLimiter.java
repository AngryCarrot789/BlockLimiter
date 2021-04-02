package reghzy.blocklimiter;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.commands.ReloadCommandCommand;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.config.FileHelper;
import reghzy.blocklimiter.config.PlayerConfig;
import reghzy.blocklimiter.limiting.BlockLimit;
import reghzy.blocklimiter.limiting.ConfigSaveTask;
import reghzy.blocklimiter.limiting.LimitManager;
import reghzy.blocklimiter.listeners.BlockListener;
import reghzy.blocklimiter.listeners.PlayerConnectionListener;
import reghzy.blocklimiter.logs.ChatLogger;

import java.io.File;

public class BlockLimiter extends JavaPlugin implements REghZyBasePlugin {
    private ChatLogger logger;
    private LimitManager limitManager;
    private ConfigSaveTask saveTask;

    private BlockListener blockListener;
    private PlayerConnectionListener playerConnectionListener;

    private static BlockLimiter instance;

    public static BlockLimiter getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        BlockLimiter.instance = this;

        getCommand("blocklimiterreload").setExecutor(new ReloadCommandCommand());

        FileHelper.ensurePluginFolderExists(this);
        PlayerConfig.PlayersDirectory = new File(this.getDataFolder(), PlayerConfig.PLAYER_CONFIG_DIRECTORY);
        PlayerConfig.PlayersDirectory.mkdir();

        logger = new ChatLogger(this, null);

        ConfigManager.initialise();

        this.limitManager = new LimitManager();
        this.limitManager.loadConfig(ConfigManager.getMainConfig());

        this.blockListener = new BlockListener(this.limitManager, this);
        this.playerConnectionListener = new PlayerConnectionListener(this.limitManager, this);

        this.saveTask = new ConfigSaveTask(this, this.limitManager);
        this.saveTask.loadConfig(ConfigManager.getMainConfig());
        this.saveTask.startTask();
    }

    public LimitManager getLimitManager() {
        return this.limitManager;
    }

    public ConfigSaveTask getSaveTask() {
        return this.saveTask;
    }

    @Override
    public void onDisable() {
        this.saveTask.stopTask();
    }

    @Override
    public ChatLogger getChatLogger() {
        return this.logger;
    }

    @Override
    public String getChatPrefix() {
        return ChatColor.AQUA + "[BlockLimit]" + ChatColor.RESET;
    }

    @Override
    public String getCommandPrefix() {
        return "bl";
    }
}
