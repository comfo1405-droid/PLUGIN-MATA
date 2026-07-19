package com.messhy.plugin.listeners;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.BanList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AntiTotemListener implements Listener {

    private final MesshyPlugin plugin;

    // rapid offhand-totem swap tracking
    private final Map<UUID, Deque<Long>> totemSwaps = new ConcurrentHashMap<>();
    // rapid resurrection tracking
    private final Map<UUID, Deque<Long>> resurrections = new ConcurrentHashMap<>();

    private static final int SWAP_LIMIT = 6;
    private static final long SWAP_WINDOW_MS = 3000L;
    private static final int RESURRECT_LIMIT = 3;
    private static final long RESURRECT_WINDOW_MS = 10000L;
    private static final long BAN_DURATION_MS = TimeUnit.DAYS.toMillis(7);

    public AntiTotemListener(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (!plugin.getDataManager().getData().settings.antiAutoTotem) return;

        Player player = event.getPlayer();
        ItemStack offhandAfter = event.getOffHandItem();
        if (offhandAfter == null || offhandAfter.getType() != Material.TOTEM_OF_UNDYING) return;

        if (recordAndCheck(totemSwaps, player.getUniqueId(), SWAP_LIMIT, SWAP_WINDOW_MS)) {
            ban(player, "auto-totem macro detected (rapid offhand swapping)");
        }
    }

    @EventHandler
    public void onResurrect(EntityResurrectEvent event) {
        if (!plugin.getDataManager().getData().settings.antiAutoTotem) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (recordAndCheck(resurrections, player.getUniqueId(), RESURRECT_LIMIT, RESURRECT_WINDOW_MS)) {
            ban(player, "auto-totem macro detected (rapid resurrection)");
        }
    }

    private boolean recordAndCheck(Map<UUID, Deque<Long>> map, UUID uuid, int limit, long windowMs) {
        long now = System.currentTimeMillis();
        Deque<Long> times = map.computeIfAbsent(uuid, k -> new ArrayDeque<>());
        times.addLast(now);
        while (!times.isEmpty() && now - times.peekFirst() > windowMs) {
            times.pollFirst();
        }
        return times.size() > limit;
    }

    private void ban(Player player, String reason) {
        String key = player.getName().toLowerCase(Locale.ROOT);
        long expiry = System.currentTimeMillis() + BAN_DURATION_MS;
        plugin.getDataManager().getData().tempBans.put(key, expiry);
        plugin.getDataManager().save();

        plugin.getServer().getBanList(BanList.Type.NAME).addBan(
                player.getName(), reason, new Date(expiry), "messhy_js AntiAutoTotem");

        plugin.getServer().getScheduler().runTask(plugin, () ->
                player.kickPlayer(ColorUtil.colorize("&cYou have been banned for 7 days: " + reason)));
    }
}
