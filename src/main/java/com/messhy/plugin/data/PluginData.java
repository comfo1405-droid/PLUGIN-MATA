package com.messhy.plugin.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PluginData {
    // username(lowercase) -> account
    public Map<String, UserAccount> users = new HashMap<>();
    // ip -> allowed registration slots
    public Map<String, Integer> ipSlots = new HashMap<>();
    // username(lowercase) -> bad word warning count
    public Map<String, Integer> warnings = new HashMap<>();
    // username(lowercase) -> ban expiry epoch millis (0 = not banned / handled by Bukkit ban list too)
    public Map<String, Long> tempBans = new HashMap<>();
    public Set<String> badWords = new HashSet<>();
    public PluginSettings settings = new PluginSettings();
}
