package reghzy.blocklimiter.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.BlockLimiter;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.helpers.PermissionsHelper;
import reghzy.blocklimiter.logs.ChatLogger;

public class ReloadCommandCommand implements CommandExecutor {
    public static String ReloadConfigPermission = "blocklimiter.command.reload";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (PermissionsHelper.isConsoleOrHasPermsOrOp(commandSender, ReloadConfigPermission)) {
            try {
                ConfigManager.getMainConfig().tryLoadYaml();

                BlockLimiter plugin = BlockLimiter.getInstance();
                plugin.getLimitManager().loadConfig(ConfigManager.getMainConfig());

                plugin.getSaveTask().loadConfig(ConfigManager.getMainConfig());
                plugin.getSaveTask().restartTask();
            }
            catch (Exception e) {
                commandSender.sendMessage(ChatColor.GOLD + "Failed to reload");
                ChatLogger.logConsole("Failed to reload");
                e.printStackTrace();
            }
        }
        else {
            commandSender.sendMessage(ChatColor.GOLD + "You dont have permission to reload the configs!");
        }
        return true;
    }
}
