package reghzy.blocklimiter.config;

import reghzy.blocklimiter.REghZyBasePlugin;

import java.io.File;

public class PlayerConfig extends Config {
    public static final String PLAYER_CONFIG_DIRECTORY = "players";

    public static File PlayersDirectory;

    public PlayerConfig(REghZyBasePlugin plugin, File configFile) {
        super(plugin, configFile);
    }

    public PlayerConfig(REghZyBasePlugin plugin, String username, boolean loadOrCreate) {
        this(plugin, new File(PlayersDirectory, username + ".yml"));

        if (loadOrCreate) {
            if (!tryLoadYaml()) {
                createNewFile();
            }
        }
    }
}
