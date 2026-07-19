package com.messhy.plugin.commands;

import com.messhy.plugin.MesshyPlugin;
import com.messhy.plugin.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private final MesshyPlugin plugin;

    public VanishCommand(MesshyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("messhy.admin")) {
            player.sendMessage(ColorUtil.colorize("&cYou do not have permission to do that."));
            return true;
        }

        boolean nowVanished;
        if (plugin.getVanished().contains(player.getUniqueId())) {
            plugin.getVanished().remove(player.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(plugin, player);
            }
            nowVanished = false;
        } else {
            plugin.getVanished().add(player.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.hasPermission("messhy.admin")) {
                    other.hidePlayer(plugin, player);
                }
            }
            nowVanished = true;
        }

        player.sendMessage(ColorUtil.colorize(nowVanished ? "&aYou are now vanished." : "&aYou are now visible."));
        return true;
    }
}
