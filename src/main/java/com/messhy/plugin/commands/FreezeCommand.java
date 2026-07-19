package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public FreezeCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /freeze <user>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ColorUtil.colorize("&cPlayer not found or not online."));
            return true;
        }

        boolean nowFrozen;
        if (plugin.getFrozen().contains(target.getUniqueId())) {
            plugin.getFrozen().remove(target.getUniqueId());
            nowFrozen = false;
        } else {
            plugin.getFrozen().add(target.getUniqueId());
            nowFrozen = true;
        }

        target.sendMessage(ColorUtil.colorize(nowFrozen ? "&cYou have been frozen by an admin." : "&aYou have been unfrozen."));
        sender.sendMessage(ColorUtil.colorize("&a" + target.getName() + " is now " + (nowFrozen ? "frozen." : "unfrozen.")));
        return true;
    }
}
