package com.messhy.plugin.util;

import org.bukkit.ChatColor;

public class ColorUtil {

    /**
     * Accepts either a plain color name (e.g. "gold", "red") or an & code (e.g. "&6"),
     * and returns the matching ChatColor, or null if not recognized.
     */
    public static ChatColor parse(String input) {
        if (input == null) return null;
        String cleaned = input.trim();

        if (cleaned.length() == 2 && cleaned.charAt(0) == '&') {
            ChatColor byCode = ChatColor.getByChar(cleaned.charAt(1));
            if (byCode != null) return byCode;
        }

        try {
            return ChatColor.valueOf(cleaned.toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
