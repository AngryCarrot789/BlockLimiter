package reghzy.blocklimiter.config;

import reghzy.blocklimiter.BlockLimiter;

public class ConfigManager {
    private static Config mainConfig;

    public static void initialise() {
        try {
            mainConfig = Config.getConfigInPlugin(BlockLimiter.getInstance(), "config.yml");
            mainConfig.createNewFileOrGetDefault();
            mainConfig.tryLoadYaml();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Config getMainConfig() {
        return mainConfig;
    }
}
