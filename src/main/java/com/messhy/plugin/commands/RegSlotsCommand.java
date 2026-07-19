package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.data.PluginData;
import com.messhy.plugin.data.UserAccount;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class RegSlotsCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public RegSlotsCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("messhy.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }
        if (args.length != 3 || !(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: /reg-slots <add|remove> <user> <slots>"));
            return true;
        }

        String action = args[0].toLowerCase();
        String targetName = args[1];
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.colorize("&cSlots must be a number."));
            return true;
        }

        String ip = resolveIp(targetName);
        if (ip == null) {
            sender.sendMessage(ColorUtil.colorize("&cCould not find that user (they must have joined at least once)."));
            return true;
        }

        PluginData data = plugin.getDataManager().getData();
        int current = data.ipSlots.getOrDefault(ip,
                data.settings.serverMode.equalsIgnoreCase("private")
                        ? data.settings.defaultPrivateSlots
                        : data.settings.defaultPublicSlots);

        int updated = action.equals("add") ? current + amount : Math.max(0, current - amount);
        data.ipSlots.put(ip, updated);
        plugin.getDataManager().save();

        sender.sendMessage(ColorUtil.colorize("&aSlots for " + targetName + "'s IP (" + ip + ") set to &f" + updated));
        return true;
    }

    private String resolveIp(String targetName) {
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null && online.getAddress() != null) {
            return online.getAddress().getAddress().getHostAddress();
        }
        UserAccount account = plugin.getDataManager().getData().users.get(targetName.toLowerCase(Locale.ROOT));
        if (account != null) {
            return account.lastIp != null ? account.lastIp : account.registeredIp;
        }
        return null;
    }
}
