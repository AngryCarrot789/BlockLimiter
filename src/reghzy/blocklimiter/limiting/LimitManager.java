package reghzy.blocklimiter.limiting;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.BlockLimiter;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.helpers.PermissionsHelper;
import reghzy.blocklimiter.limiting.player.PlayerBlockCounter;
import reghzy.blocklimiter.limiting.utils.IntegerRange;
import reghzy.blocklimiter.logs.ChatFormat;
import reghzy.blocklimiter.logs.ChatLogger;

import java.util.HashMap;

public class LimitManager {
    // id -> limit
    private final HashMap<Integer, BlockLimit> limits;
    // full username -> counter
    private final HashMap<String, PlayerBlockCounter> counters;

    public static final String LimitSectionName = "limits";
    public static final String BlockDataName = "MetaData";
    public static final String BypassEverythingName = "BypassEverythingPermission";
    public static final String PermissionName = "Permission";
    public static final String DenyMessageName = "DenyMessage";
    public static String BypassEverythingPermission;

    public LimitManager() {
        this.limits = new HashMap<Integer, BlockLimit>(16);
        this.counters = new HashMap<String, PlayerBlockCounter>(40);
    }

    public void loadConfig(Config config) {
        if (!config.tryLoadYaml()) {
            BlockLimiter.getInstance().getChatLogger().logPlugin("Failed to reload LimitManager config");
            return;
        }
        BypassEverythingPermission = config.getString(BypassEverythingName);
        ConfigurationSection limitsSection = config.getConfigurationSection(LimitSectionName);
        if (limitsSection == null) {
            limitsSection = config.createSection(LimitSectionName);
        }

        this.limits.clear();
        for(String limitedId : limitsSection.getKeys(false)) {
            int id = Integer.parseInt(limitedId);
            ConfigurationSection limit = limitsSection.getConfigurationSection(limitedId);
            boolean foundData = false;
            boolean foundBypass = false;
            BlockLimit blockLimit = new BlockLimit(id, -1, null);
            for(String key : limit.getKeys(false)) {
                if (!foundData && key.equals(BlockDataName)) {
                    blockLimit.metaData = limit.getInt(BlockDataName, -1);
                    foundData = true;
                }
                else if (!foundBypass && key.equals(BlockLimit.BypassPermissionName)) {
                    blockLimit.bypassPermission = limit.getString(key, "");
                    foundBypass = true;
                }
                else {
                    ConfigurationSection limitSection = limit.getConfigurationSection(key);
                    String permission = limitSection.getString(PermissionName);
                    String denyMessage = limitSection.getString(DenyMessageName);
                    IntegerRange range = new IntegerRange(key);
                    blockLimit.addRangePermission(range, permission, denyMessage);
                }
            }

            limits.put(id, blockLimit);
        }

        BlockLimiter.getInstance().getChatLogger().logPlugin("Loaded " + this.limits.size() + " limits");
    }

    public BlockLimit getLimit(Block block) {
        return limits.get(block.getTypeId());
    }

    public BlockLimit getLimit(int id) {
        return limits.get(id);
    }

    public HashMap<String, PlayerBlockCounter> getPlayerCounters() {
        return this.counters;
    }

    public boolean shouldCancelBlockPlace(Player player, Block block) {
        BlockLimit limit = getLimit(block);
        if (limit == null)
            return false;
        if (limit.matchBlock(block)) {
            if (PermissionsHelper.hasPermission(player, BypassEverythingPermission)) {
                return false;
            }
            if (limit.playerBypasses(player)) {
                return false;
            }

            String username = player.getName();
            PlayerBlockCounter counter = counters.get(username);
            if (counter == null) {
                ChatLogger.logConsole("Fatal error: player " + ChatFormat.apostrophise(username) + " did not have a block counter when they should have");
                counter = new PlayerBlockCounter(player, username);
                counters.put(username, counter);
            }

            return !counter.tryPlaceLimitedBlock(limit);
        }
        return false;
    }

    public boolean shouldCancelBlockBreak(Player player, Block block) {
        BlockLimit limit = getLimit(block);
        if (limit == null)
            return false;
        if (limit.matchBlock(block)) {
            if (PermissionsHelper.hasPermission(player, BypassEverythingPermission)) {
                return false;
            }
            if (limit.playerBypasses(player)) {
                return false;
            }

            String username = player.getName();
            PlayerBlockCounter counter = counters.get(username);
            if (counter == null) {
                ChatLogger.logConsole("Fatal error: player " + ChatFormat.apostrophise(username) + " did not have a block counter when they should have");
                counter = new PlayerBlockCounter(player, username);
                counters.put(username, counter);
            }

            return !counter.tryBreakLimitedBlock(limit);
        }

        return false;
    }

    public void loadPlayer(Player player) {
        String username = player.getName();
        PlayerBlockCounter counter = counters.get(username);
        if (counter == null) {
            BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.GREEN + ChatFormat.apostrophise(username) + " has joined... Loading their placement config");
            counter = new PlayerBlockCounter(player, username);
            counters.put(username, counter);
        }
        else {
            BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.RED + "Fatal error: player " + ChatFormat.apostrophise(username) + " was already loaded when they shouldn't be");
        }
    }

    public void unloadPlayer(Player player) {
        String username = player.getName();
        PlayerBlockCounter counter = this.counters.get(username);
        if (counter == null) {
            BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.RED + "Fatal error: player " + ChatFormat.apostrophise(username) + " was not loaded when they should've been");
            return;
        }

        BlockLimiter.getInstance().getChatLogger().logPlugin(ChatColor.GREEN + ChatFormat.apostrophise(username) + " has left. Saved their placement config");
        counter.saveConfig();
        counters.remove(username);
    }

    public static void sendDenyMessage(Player player, String message, BlockLimit limiter) {
        player.sendMessage(BlockLimiter.getInstance().getChatPrefix() + " " + translateWildcards(message, limiter, player));
    }

    public static String translateWildcards(String message, BlockLimit limiter, Player player) {
        StringBuilder newMessage = new StringBuilder(message.length() * 4);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '%') {
                newMessage.append(getLimiterWildcard(message.charAt(++i), limiter, player));
            }
            else if (c == '&') {
                newMessage.append((char) 167).append(message.charAt(++i));
            }
            else {
                newMessage.append(c);
            }
        }
        return newMessage.toString();
    }

    public static String getLimiterWildcard(char wildcard, BlockLimit limiter, Player player) {
        if (wildcard == 'd')
            return String.valueOf(limiter.id);
        if (wildcard == 'm')
            return String.valueOf(limiter.metaData);
        if (wildcard == 'p')
            return String.valueOf(player.getName());
        return "";
    }
}
