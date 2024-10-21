package me.pm7.minihardcore.Commands;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.pm7.minihardcore.MiniHardcore.started;

public class Vote implements CommandExecutor {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(started && !config.getBoolean("Endgame")) {
            if(sender instanceof Player) {
                HashSet<String> dead = plugin.getDead();
                if(!dead.contains(sender.getName())) {
                    Player p = (Player) sender;
                    Inventory inventory = Bukkit.createInventory(p, 18, ChatColor.RED + "Vote Menu");
                    int i = 0;
                    for (String playerName : dead) {
                        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
                        ItemMeta itemMeta = item.getItemMeta();
                        if (find(sender.getName(), playerName)) {
                            item.setType(Material.RED_STAINED_GLASS_PANE);
                            itemMeta.setLore(Collections.singletonList("Click here"));
                        } else {
                            item.setType(Material.LIME_STAINED_GLASS_PANE);
                            itemMeta.setLore(Collections.singletonList("Click here"));
                        }
                        itemMeta.setDisplayName(playerName);
                        item.setItemMeta(itemMeta);
                        inventory.setItem(i, item);
                        i++;
                    }
                    p.openInventory(inventory);
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "The game has not started yet");
        }
        return true;
    }
    boolean find(String senderName, String deadName) {
        ArrayList<String> VoteList1 = plugin.getVoteList1();
        ArrayList<String> VoteList2 = plugin.getVoteList2();
        for(int i=0; i<VoteList1.size(); i++) {
            String name = VoteList1.get(i);
            if(Objects.equals(name, senderName)) {
               if(Objects.equals(VoteList2.get(i), deadName)) {
                   return true;
               }
            }
        }
        return false;
    }
}
