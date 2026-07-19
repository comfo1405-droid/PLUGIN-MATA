package com.messhy.plugin.listeners;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private final MesshyPlugin plugin;

    // spam tracking: recent message send timestamps per player
    private final Map<UUID, Deque<Long>> recentMessages = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastMessage = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();

    private static final int SPAM_MESSAGE_LIMIT = 5;     // max messages
    private static final long SPAM_WINDOW_MS = 4000L;    // within this window
    private static final long DUPLICATE_COOLDOWN_MS = 2000L; // repeating same msg this fast = spam
    private static final int MAX_WARNINGS_BEFORE_KICK = 3;

    public ChatListener(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!plugin.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(ColorUtil.colorize("&cYou must log in before chatting. Use /login or /register."));
            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();

        // ---- anti-spam ----
        if (plugin.getDataManager().getData().settings.antiSpam) {
            if (isSpamming(player, message)) {
                event.setCancelled(true);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        player.kickPlayer(ColorUtil.colorize("&cKicked for spamming.")));
                return;
            }
        }

        // ---- bad word filter ----
        String badWord = containsBadWord(message);
        if (badWord != null) {
            event.setCancelled(true);
            String key = player.getName().toLowerCase(Locale.ROOT);
            int count = plugin.getDataManager().getData().warnings.merge(key, 1, Integer::sum);
            plugin.getDataManager().save();
            player.sendMessage(ColorUtil.colorize("&cWarning (" + count + "/" + MAX_WARNINGS_BEFORE_KICK
                    + "): your message contained a blocked word."));
            if (count >= MAX_WARNINGS_BEFORE_KICK) {
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        player.kickPlayer(ColorUtil.colorize("&cKicked for repeated use of blocked words.")));
            }
        }
    }

    private boolean isSpamming(Player player, String message) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        // duplicate message sent too quickly
        String last = lastMessage.get(uuid);
        Long lastTime = lastMessageTime.get(uuid);
        if (last != null && last.equalsIgnoreCase(message) && lastTime != null
                && (now - lastTime) < DUPLICATE_COOLDOWN_MS) {
            return true;
        }
        lastMessage.put(uuid, message);
        lastMessageTime.put(uuid, now);

        // too many messages within the window
        Deque<Long> times = recentMessages.computeIfAbsent(uuid, k -> new ArrayDeque<>());
        times.addLast(now);
        while (!times.isEmpty() && now - times.peekFirst() > SPAM_WINDOW_MS) {
            times.pollFirst();
        }
        return times.size() > SPAM_MESSAGE_LIMIT;
    }

    private String containsBadWord(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        for (String word : plugin.getDataManager().getData().badWords) {
            if (lower.contains(word.toLowerCase(Locale.ROOT))) {
                return word;
            }
        }
        return null;
    }
}
