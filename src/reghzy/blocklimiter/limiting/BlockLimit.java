package reghzy.blocklimiter.limiting;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.helpers.PermissionsHelper;
import reghzy.blocklimiter.limiting.utils.IntegerRange;
import reghzy.blocklimiter.limiting.utils.PermissionMessagePair;

import java.util.HashMap;

public class BlockLimit {
    public int id;
    public int metaData;

    // key: placement limit range
    // string: permission
    private final HashMap<IntegerRange, PermissionMessagePair> placementPermissions;
    private final HashMap<Integer, IntegerRange> rangeCache;

    public String bypassPermission;

    public static final String BypassPermissionName = "BypassPermission";

    public BlockLimit(int id, int metaData, String bypassPermission) {
        this.id = id;
        this.metaData = metaData;
        this.bypassPermission = bypassPermission;
        this.placementPermissions = new HashMap<IntegerRange, PermissionMessagePair>(4);
        this.rangeCache = new HashMap<Integer, IntegerRange>(16);
    }

    public void addRangePermission(IntegerRange range, String permission, String denyMessage) {
        this.placementPermissions.put(range, new PermissionMessagePair(permission, denyMessage));
        for(int i = range.min; i <= range.max; i++) {
            rangeCache.put(i, range);
        }
    }

    public boolean matchBlock(Block block) {
        return this.id == block.getTypeId() && (this.metaData == -1 || this.metaData == block.getData());
    }

    public boolean canPlayerPlace(Player player, int currentlyPlaced) {
        IntegerRange range = getPlacementRangeFromBetween(currentlyPlaced);
        if (range == null)
            return true;

        PermissionMessagePair pair = placementPermissions.get(range);
        if (pair == null)
            return true;

        if (PermissionsHelper.hasPermission(player, pair.permission)) {
            return true;
        }
        else {
            LimitManager.sendDenyMessage(player, pair.denyMessage, this);
            return false;
        }
    }

    public IntegerRange getPlacementRangeFromBetween(int value) {
        return rangeCache.get(value);
    }

    public boolean playerBypasses(Player player) {
        return PermissionsHelper.hasPermission(player, bypassPermission);
    }

    @Override
    public int hashCode() {
        return this.id + (this.metaData << 12) + (this.placementPermissions.size() << 20);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockLimit) {
            BlockLimit limit = (BlockLimit) obj;
            return limit.id == this.id &&
                   limit.metaData == this.metaData;
        }
        return false;
    }
}
