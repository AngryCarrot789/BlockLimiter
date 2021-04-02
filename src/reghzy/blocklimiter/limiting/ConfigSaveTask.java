package reghzy.blocklimiter.limiting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import reghzy.blocklimiter.BlockLimiter;
import reghzy.blocklimiter.REghZyBasePlugin;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.limiting.player.PlayerBlockCounter;

import java.util.HashMap;

public class ConfigSaveTask implements Runnable {
    private REghZyBasePlugin plugin;
    private int taskId;
    private LimitManager limitManager;
    private HashMap<String, PlayerBlockCounter> playerCounters;
    private int delayTicks;

    public static final String DelayTicksName = "Save All Configs Interval";

    public ConfigSaveTask(REghZyBasePlugin plugin, LimitManager manager) {
        this.plugin = plugin;
        this.limitManager = manager;
        this.playerCounters = this.limitManager.getPlayerCounters();
    }

    public void loadConfig(Config mainConfig) {
        this.delayTicks = mainConfig.getInt(DelayTicksName);
    }

    public void startTask() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) plugin, this, 0, delayTicks);
    }

    public void stopTask() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    public void restartTask() {
        stopTask();
        startTask();
    }

    @Override
    public void run() {
        if (playerCounters == null || playerCounters.size() == 0) {
            //BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.GOLD + "No players available to save");
            return;
        }

        //BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.GOLD + "Saving player placements...");
        int count = 0;
        for(PlayerBlockCounter counter : playerCounters.values()) {
            counter.saveConfig();
            count++;
        }
        //BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.GREEN + "Saved " + count + " players!");
    }
}
