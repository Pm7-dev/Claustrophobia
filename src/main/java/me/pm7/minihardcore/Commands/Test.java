package me.pm7.minihardcore.Commands;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static me.pm7.minihardcore.MiniHardcore.started;

public class Test implements CommandExecutor {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(started) {
            Player p = (Player) sender;
            HashMap<String, Integer> Janbonium = plugin.getJanbonium();

            if(Janbonium.containsKey(p.getName())) {
                Janbonium.replace(p.getName(), 9999999);
            } else {
                Janbonium.put(p.getName(), 9999999);
            }
            plugin.setJanbonium(Janbonium);

            ((Player) sender).setInvisible(false);
            ((Player) sender).setInvulnerable(false);
            ((Player) sender).setAllowFlight(false);

        }
        return true;
    }
}
