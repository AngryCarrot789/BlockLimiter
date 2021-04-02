package reghzy.blocklimiter.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import reghzy.blocklimiter.REghZyBasePlugin;
import reghzy.blocklimiter.limiting.LimitManager;

public class BlockListener extends BaseListener implements Listener {
    private final LimitManager limitManager;

    public BlockListener(LimitManager limitManager, REghZyBasePlugin plugin) {
        super(plugin);
        this.limitManager = limitManager;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getBlock() == null)
            return;

        if (limitManager.shouldCancelBlockBreak(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getBlock() == null)
            return;

        if (limitManager.shouldCancelBlockPlace(player, event.getBlock())) {
            event.setCancelled(true);
        }
    }
}