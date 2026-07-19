package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ServerModeCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public ServerModeCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 1 || !(args[0].equalsIgnoreCase("private") || args[0].equalsIgnoreCase("public"))) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /server <private|public>"));
            return true;
        }

        String mode = args[0].toLowerCase();
        plugin.getDataManager().getData().settings.serverMode = mode;
        plugin.getDataManager().save();

        int defaultSlots = mode.equals("private")
                ? plugin.getDataManager().getData().settings.defaultPrivateSlots
                : plugin.getDataManager().getData().settings.defaultPublicSlots;

        sender.sendMessage(ColorUtil.colorize("&aServer registration mode set to &f" + mode
                + "&a. Default slots per IP: &f" + defaultSlots));
        return true;
    }
}
