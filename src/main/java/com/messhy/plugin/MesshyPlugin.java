package com.messhy.plugin;

import com.messhy.plugin.commands.*;
import com.messhy.plugin.data.DataManager;
import com.messhy.plugin.listeners.AntiCheatListener;
import com.messhy.plugin.listeners.AntiTotemListener;
import com.messhy.plugin.listeners.AuthListener;
import com.messhy.plugin.listeners.ChatListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MesshyPlugin extends JavaPlugin {

    private DataManager dataManager;

    // runtime (not persisted) session state
    private final Set<UUID> loggedIn = ConcurrentHashMap.newKeySet();
    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();
    private final Set<UUID> frozen = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        this.dataManager = new DataManager(this);

        // listeners
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new AntiCheatListener(this), this);
        getServer().getPluginManager().registerEvents(new AntiTotemListener(this), this);

        // commands
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("server").setExecutor(new ServerModeCommand(this));
        getCommand("reg-slots").setExecutor(new RegSlotsCommand(this));
        getCommand("announcement").setExecutor(new AnnouncementCommand(this));
        getCommand("user-say").setExecutor(new UserSayCommand(this));
        getCommand("anti-spam").setExecutor(new ToggleCommands(this, ToggleCommands.Type.ANTI_SPAM));
        getCommand("anti-cheating").setExecutor(new ToggleCommands(this, ToggleCommands.Type.ANTI_CHEATING));
        getCommand("anti-auto-totem").setExecutor(new ToggleCommands(this, ToggleCommands.Type.ANTI_AUTO_TOTEM));
        getCommand("bad-word").setExecutor(new BadWordCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("warnings").setExecutor(new WarningsCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));

        getLogger().info("messhy_js has been enabled.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
        getLogger().info("messhy_js has been disabled.");
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public Set<UUID> getLoggedIn() {
        return loggedIn;
    }

    public Set<UUID> getVanished() {
        return vanished;
    }

    public Set<UUID> getFrozen() {
        return frozen;
    }

    public boolean isLoggedIn(UUID uuid) {
        return loggedIn.contains(uuid);
    }
}
