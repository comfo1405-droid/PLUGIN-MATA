package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BadWordCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public BadWordCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 2 || !args[0].equalsIgnoreCase("add")) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /bad-word add <word>"));
            return true;
        }

        String word = args[1].toLowerCase();
        boolean added = plugin.getDataManager().getData().badWords.add(word);
        plugin.getDataManager().save();

        if (added) {
            sender.sendMessage(ColorUtil.colorize("&aAdded \"" + word + "\" to the bad word list."));
        } else {
            sender.sendMessage(ColorUtil.colorize("&e\"" + word + "\" is already in the bad word list."));
        }
        return true;
    }
}
