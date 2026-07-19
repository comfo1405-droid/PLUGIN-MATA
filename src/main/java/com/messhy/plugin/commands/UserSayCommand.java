package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserSayCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public UserSayCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /user-say <user> <message> <color>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ColorUtil.colorize("&cPlayer not found or not online."));
            return true;
        }

        String colorArg = args[args.length - 1];
        ChatColor color = ColorUtil.parse(colorArg);
        if (color == null) {
            sender.sendMessage(ColorUtil.colorize("&cUnknown color: " + colorArg));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length - 1; i++) {
            sb.append(args[i]).append(' ');
        }
        String message = sb.toString().trim();

        target.sendMessage(color + message);
        sender.sendMessage(ColorUtil.colorize("&aMessage sent to " + target.getName() + "."));
        return true;
    }
}
