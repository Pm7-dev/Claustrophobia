package me.pm7.minihardcore.Commands;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import static me.pm7.minihardcore.MiniHardcore.started;

public class Start implements CommandExecutor {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("sockdrawer")) {
            if(!started) {
                started = true;
                plugin.getConfig().set("Started", true);
                plugin.saveConfig();
                Bukkit.broadcastMessage(ChatColor.RED + "started");
                config.set("Players", Bukkit.getOnlinePlayers().size());
            }else{
                sender.sendMessage(ChatColor.RED + "The game has already started");
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Insufficient permission");
        }
        return true;
    }
}
