package com.messhy.plugin.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.messhy.plugin.MesshyPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class DataManager {

    private final MesshyPlugin plugin;
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private PluginData data;

    public DataManager(MesshyPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "user_info.json");
        load();
    }

    public PluginData getData() {
        return data;
    }

    public synchronized void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!file.exists()) {
            data = new PluginData();
            save();
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            PluginData loaded = gson.fromJson(reader, PluginData.class);
            data = (loaded != null) ? loaded : new PluginData();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load user_info.json: " + e.getMessage());
            data = new PluginData();
        }
    }

    public synchronized void save() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save user_info.json: " + e.getMessage());
        }
    }

    // ---- password hashing (SHA-256 with per-user salt) ----

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hashed = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
