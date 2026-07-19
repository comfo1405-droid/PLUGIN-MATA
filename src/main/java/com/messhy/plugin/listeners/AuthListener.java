package com.messhy.plugin.listeners;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.data.UserAccount;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthListener implements Listener {

    // players still in the process of logging in / registering (not yet authenticated)
    private final Set<UUID> pending = ConcurrentHashMap.newKeySet();
    private final MesshyPlugin plugin;
    private static final long LOGIN_TIMEOUT_TICKS = 60L * 20L; // 60 seconds

    // commands allowed before authentication (without the leading slash)
    private static final Set<String> ALLOWED_PRE_AUTH = Set.of("login", "register", "l", "reg");

    public AuthListener(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String key = player.getName().toLowerCase(Locale.ROOT);
        pending.add(player.getUniqueId());
        plugin.getLoggedIn().remove(player.getUniqueId());

        UserAccount account = plugin.getDataManager().getData().users.get(key);
        if (account == null) {
            player.sendMessage(ColorUtil.colorize("&eThis account is not registered."));
            player.sendMessage(ColorUtil.colorize("&eUse &f/register <password> <confirmPassword>&e to create one."));
        } else {
            player.sendMessage(ColorUtil.colorize("&eWelcome back! Use &f/login <password>&e to continue."));
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && !plugin.isLoggedIn(player.getUniqueId())) {
                player.kickPlayer(ColorUtil.colorize("&cYou took too long to log in."));
            }
        }, LOGIN_TIMEOUT_TICKS);

        // hide any currently-vanished admins from the newly joined player
        for (UUID vanishedUuid : plugin.getVanished()) {
            Player vanishedPlayer = plugin.getServer().getPlayer(vanishedUuid);
            if (vanishedPlayer != null && !player.hasPermission("messhy.admin")) {
                player.hidePlayer(plugin, vanishedPlayer);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        pending.remove(uuid);
        plugin.getLoggedIn().remove(uuid);
        plugin.getFrozen().remove(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean needsAuth = !plugin.isLoggedIn(player.getUniqueId());
        boolean frozen = plugin.getFrozen().contains(player.getUniqueId());
        if (!needsAuth && !frozen) return;

        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getZ() != event.getTo().getZ()
                || event.getFrom().getY() != event.getTo().getY()) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.isLoggedIn(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.isLoggedIn(player.getUniqueId())) return;

        String[] parts = event.getMessage().substring(1).split(" ");
        String cmd = parts[0].toLowerCase(Locale.ROOT);
        if (!ALLOWED_PRE_AUTH.contains(cmd)) {
            player.sendMessage(ColorUtil.colorize("&cYou must log in first. Use /login or /register."));
            event.setCancelled(true);
        }
    }

    public void markLoggedIn(Player player) {
        pending.remove(player.getUniqueId());
        plugin.getLoggedIn().add(player.getUniqueId());
    }
}
