package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class WarningsCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public WarningsCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /warnings <user>"));
            return true;
        }

        int count = plugin.getDataManager().getData().warnings.getOrDefault(args[0].toLowerCase(Locale.ROOT), 0);
        sender.sendMessage(ColorUtil.colorize("&e" + args[0] + " has &f" + count + "&e bad-word warning(s)."));
        return true;
    }
}
