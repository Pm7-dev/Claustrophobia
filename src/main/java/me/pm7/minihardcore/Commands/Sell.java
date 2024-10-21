package me.pm7.minihardcore.Commands;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.pm7.minihardcore.MiniHardcore.started;

public class Sell implements CommandExecutor {
    MiniHardcore plugin = MiniHardcore.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(started) {
            if(sender instanceof Player) {
                Player p = (Player) sender;

                HashMap<String, Integer> Janbonium = plugin.getJanbonium();
                for(ItemStack i : p.getInventory().getContents()) {
                    if (i != null) {
                        if(i.getType() == Material.LAVA_BUCKET) {
                            if(Janbonium.containsKey(p.getName())) {
                                int janbonium = Janbonium.get(p.getName());
                                janbonium = janbonium + 100;
                                Janbonium.replace(p.getName(), janbonium);
                            } else {
                                Janbonium.put(p.getName(), 100);
                            }
                            i.setType(Material.BUCKET);
                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have any lava buckets");
                        }
                    }
                }
                plugin.setJanbonium(Janbonium);
                plugin.loadScoreBoard(p);
            }
        }else{
            sender.sendMessage(ChatColor.RED + "The game has not started yet");
        }
        return true;
    }
}
