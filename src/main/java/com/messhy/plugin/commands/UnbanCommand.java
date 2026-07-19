package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class UnbanCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public UnbanCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /unban <user>"));
            return true;
        }

        Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
        plugin.getDataManager().getData().tempBans.remove(args[0].toLowerCase(Locale.ROOT));
        plugin.getDataManager().save();

        sender.sendMessage(ColorUtil.colorize("&a" + args[0] + " has been unbanned."));
        return true;
    }
}
