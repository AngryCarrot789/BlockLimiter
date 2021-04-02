package reghzy.blocklimiter.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import reghzy.blocklimiter.REghZyBasePlugin;
import reghzy.blocklimiter.limiting.LimitManager;

public class PlayerConnectionListener extends BaseListener implements Listener {
    private final LimitManager limitManager;

    public PlayerConnectionListener(LimitManager limitManager, REghZyBasePlugin plugin) {
        super(plugin);
        this.limitManager = limitManager;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        limitManager.loadPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        limitManager.unloadPlayer(player);
    }
}