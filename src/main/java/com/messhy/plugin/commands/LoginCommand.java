package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.data.DataManager;
import com.messhy.plugin.data.UserAccount;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class LoginCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public LoginCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (plugin.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(ColorUtil.colorize("&aYou are already logged in."));
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ColorUtil.colorize("&cUsage: /login <password>"));
            return true;
        }

        String key = player.getName().toLowerCase(Locale.ROOT);
        UserAccount account = plugin.getDataManager().getData().users.get(key);
        if (account == null) {
            player.sendMessage(ColorUtil.colorize("&cThis account is not registered. Use /register <password> <confirmPassword>."));
            return true;
        }

        String hashed = DataManager.hash(args[0], account.salt);
        if (!hashed.equals(account.passwordHash)) {
            player.sendMessage(ColorUtil.colorize("&cIncorrect password."));
            return true;
        }

        account.lastLogin = System.currentTimeMillis();
        account.lastIp = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : account.lastIp;
        plugin.getDataManager().save();

        plugin.getLoggedIn().add(player.getUniqueId());
        player.sendMessage(ColorUtil.colorize("&aYou have successfully logged in!"));
        return true;
    }
}
