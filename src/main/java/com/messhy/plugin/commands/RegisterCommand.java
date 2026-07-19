package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.data.DataManager;
import com.messhy.plugin.data.PluginData;
import com.messhy.plugin.data.UserAccount;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class RegisterCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public RegisterCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length != 2) {
            player.sendMessage(ColorUtil.colorize("&cUsage: /register <password> <confirmPassword>"));
            return true;
        }

        String key = player.getName().toLowerCase(Locale.ROOT);
        PluginData data = plugin.getDataManager().getData();

        if (data.users.containsKey(key)) {
            player.sendMessage(ColorUtil.colorize("&cThis account is already registered. Use /login <password>."));
            return true;
        }
        if (!args[0].equals(args[1])) {
            player.sendMessage(ColorUtil.colorize("&cPasswords do not match."));
            return true;
        }
        if (args[0].length() < 4) {
            player.sendMessage(ColorUtil.colorize("&cPassword must be at least 4 characters long."));
            return true;
        }

        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown";

        int allowedSlots = data.ipSlots.getOrDefault(ip,
                data.settings.serverMode.equalsIgnoreCase("private")
                        ? data.settings.defaultPrivateSlots
                        : data.settings.defaultPublicSlots);

        long currentAccounts = data.users.values().stream()
                .filter(u -> ip.equals(u.registeredIp))
                .count();

        if (currentAccounts >= allowedSlots) {
            player.sendMessage(ColorUtil.colorize("&cRegistration limit reached for your IP address ("
                    + currentAccounts + "/" + allowedSlots + "). Ask an admin for more slots with /reg-slots add."));
            return true;
        }

        String salt = DataManager.generateSalt();
        String hash = DataManager.hash(args[0], salt);
        UserAccount account = new UserAccount(player.getName(), player.getUniqueId().toString(), hash, salt, ip);
        account.lastLogin = System.currentTimeMillis();
        data.users.put(key, account);
        plugin.getDataManager().save();

        plugin.getLoggedIn().add(player.getUniqueId());
        player.sendMessage(ColorUtil.colorize("&aAccount registered successfully! You are now logged in."));
        return true;
    }
}
