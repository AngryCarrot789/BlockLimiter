package reghzy.blocklimiter.limiting.player;

import org.bukkit.entity.Player;
import reghzy.blocklimiter.BlockLimiter;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.config.PlayerConfig;
import reghzy.blocklimiter.limiting.BlockLimit;
import reghzy.blocklimiter.limiting.utils.ReferenceInteger;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerBlockCounter {
    /**
     * A hashmap containing a list of limits that have been placed by this player
     * Key is the limit, value is the number of blocks placed
     */
    public HashMap<BlockLimit, ReferenceInteger> limitsPlaced;

    private final PlayerConfig config;

    public final Player player;
    public final String username;
    public final String usernameLowercase;

    public boolean changesToConfigMade = true;

    public static final String PlacedName = "Placed";

    public PlayerBlockCounter(Player player, String username) {
        this.player = player;
        this.username = username;
        this.usernameLowercase = this.username.toLowerCase();

        this.config = new PlayerConfig(BlockLimiter.getInstance(), this.usernameLowercase, true);
        this.limitsPlaced = new HashMap<BlockLimit, ReferenceInteger>();

        loadFromConfig(this.config);
    }

    public void loadFromConfig(Config config) {
        this.limitsPlaced.clear();
        for(String idString : config.getKeys(false)) {
            int id = Integer.parseInt(idString);
            int placed = config.getInt(idString);

            BlockLimit limit = BlockLimiter.getInstance().getLimitManager().getLimit(id);
            if (limit == null) {
                continue;
            }

            this.limitsPlaced.put(limit, new ReferenceInteger(placed));
        }

        changesToConfigMade = true;
    }

    public boolean tryPlaceLimitedBlock(BlockLimit limit) {
        ReferenceInteger placed = limitsPlaced.get(limit);
        if (placed == null) {
            placed = new ReferenceInteger(0);
            limitsPlaced.put(limit, placed);
        }

        if (limit.canPlayerPlace(player, placed.value + 1)) {
            placed.increment();
            this.config.set(String.valueOf(limit.id), placed.value);
            changesToConfigMade = true;
            return true;
        }
        return false;
    }

    public boolean tryBreakLimitedBlock(BlockLimit limit) {
        ReferenceInteger placed = limitsPlaced.get(limit);
        if (placed == null) {
            placed = new ReferenceInteger(0);
            limitsPlaced.put(limit, placed);
        }

        if (placed.value > 0) {
            placed.decrement();
            this.config.set(String.valueOf(limit.id), placed.value);
            changesToConfigMade = true;
        }

        return true;
    }

    public void saveConfig() {
        if (this.changesToConfigMade) {
            if (this.config.trySaveYaml()) {
                this.changesToConfigMade = false;
            }
        }
    }
}
