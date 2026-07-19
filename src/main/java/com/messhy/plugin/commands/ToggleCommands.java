package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleCommands implements CommandExecutor {

    public enum Type {
        ANTI_SPAM("Anti-Spam"),
        ANTI_CHEATING("Anti-Cheating"),
        ANTI_AUTO_TOTEM("Anti-Auto-Totem");

        final String display;

        Type(String display) {
            this.display = display;
        }
    }

    private final MesshyPlugin plugin;
    private final Type type;

    public ToggleCommands(MesshyPlugin plugin, Type type) {
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 1 || !(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable"))) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /" + label + " <enable|disable>"));
            return true;
        }

        boolean enable = args[0].equalsIgnoreCase("enable");
        switch (type) {
            case ANTI_SPAM -> plugin.getDataManager().getData().settings.antiSpam = enable;
            case ANTI_CHEATING -> plugin.getDataManager().getData().settings.antiCheating = enable;
            case ANTI_AUTO_TOTEM -> plugin.getDataManager().getData().settings.antiAutoTotem = enable;
        }
        plugin.getDataManager().save();

        sender.sendMessage(ColorUtil.colorize("&a" + type.display + " has been "
                + (enable ? "&2enabled" : "&4disabled") + "&a."));
        return true;
    }
}
