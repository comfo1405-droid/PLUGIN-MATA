package com.messhy.plugin.listeners;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NOTE: This is a lightweight, easily-extendable heuristic check, not a replacement
 * for a full anti-cheat plugin (e.g. Matrix/Grim/NCP) which use packet-level analysis.
 * It flags obvious, sustained unauthorized flight as a starting point you can build on.
 */
public class AntiCheatListener implements Listener {

    private final MesshyPlugin plugin;
    private final Map<UUID, Integer> suspiciousFlightTicks = new ConcurrentHashMap<>();

    private static final int FLIGHT_VIOLATION_THRESHOLD = 20; // consecutive suspicious moves

    public AntiCheatListener(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getDataManager().getData().settings.antiCheating) return;

        Player player = event.getPlayer();
        if (!plugin.isLoggedIn(player.getUniqueId())) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.isFlying() || player.getAllowFlight()) return;
        if (player.isInsideVehicle() || player.isGliding() || player.isRiptiding()) return;

        double deltaY = event.getTo().getY() - event.getFrom().getY();
        UUID uuid = player.getUniqueId();

        boolean noFallReason = player.isInWater() || player.isClimbing() || player.hasPotionEffect(
                org.bukkit.potion.PotionEffectType.LEVITATION) || player.hasPotionEffect(
                org.bukkit.potion.PotionEffectType.JUMP_BOOST);

        // sustained upward movement without an obvious cause suggests fly-hacking
        if (deltaY > 0.35 && !player.isOnGround() && !noFallReason) {
            int ticks = suspiciousFlightTicks.merge(uuid, 1, Integer::sum);
            if (ticks >= FLIGHT_VIOLATION_THRESHOLD) {
                suspiciousFlightTicks.remove(uuid);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.teleport(event.getFrom());
                    player.kickPlayer(ColorUtil.colorize("&cKicked: suspected unauthorized flight."));
                });
            }
        } else {
            suspiciousFlightTicks.merge(uuid, -1, (a, b) -> Math.max(0, a + b));
        }
    }
}
