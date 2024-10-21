package me.pm7.minihardcore.Listeners;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.HashSet;

import static me.pm7.minihardcore.MiniHardcore.started;

public class PlayerDeathListener implements Listener {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onPLayerDeath(PlayerDeathEvent e) {
        if(started) {
            ArrayList<String> VoteList1 = plugin.getVoteList1();
            ArrayList<String> VoteList2 = plugin.getVoteList2();
            Player p = e.getEntity();

            HashSet<String> dead = plugin.getDead();
            dead.add(p.getName());
            plugin.setDead(dead);

            p.setInvulnerable(true);
            p.setInvisible(true);
            p.setAllowFlight(true);
            p.setFoodLevel(20);
            p.setHealth(20);
            RandomizeDeathMessage(p);

            config.set("Players", config.getInt("Players") - 1);
            plugin.saveConfig();

            int size = VoteList1.size();
            for(int i = size; i > 0; i--) {
                String entry = VoteList1.get(i-1);
                if(entry.equals(p.getName())) {
                    VoteList1.remove(i-1);
                    VoteList2.remove(i-1);
                }
            }
            if(config.getInt("Players") == 0) {

            }
            plugin.setVoteList1(VoteList1);
            plugin.setVoteList2(VoteList2);
        }
    }

    void RandomizeDeathMessage(Player player) {
        int messageCount = 20;
        int messageID = (int)Math.floor(Math.random()*messageCount+1);
        String title = (ChatColor.YELLOW + "Error");
        String subtitle = (ChatColor.YELLOW + "Error");
        switch (messageID) {
            case 1:
                title = (ChatColor.RED + "Socked!");
                subtitle = (ChatColor.RED + "Nothing out of the ordinary, I mean");
                break;
            case 2:
                title = (ChatColor.RED + "Aaarg!");
                subtitle = (ChatColor.RED + "You just walked the prank!");
                break;
            case 3:
                title = (ChatColor.RED + "Holy Hell!");
                subtitle = (ChatColor.RED + "Google 'out of health'");
                break;
            case 4:
                title = (ChatColor.RED + "Plinko!");
                subtitle = (ChatColor.RED + "But sir, League of Legends-");
                break;
            case 5:
                title = (ChatColor.RED + "Do the Mario!");
                subtitle = (ChatColor.RED + "Swing your arms, from side to side");
                break;
            case 6:
                title = (ChatColor.RED + "Fixing Good");
                subtitle = (ChatColor.RED + "*Worse carrier pigeon saul theme plays*");
                break;
            case 7:
                title = (ChatColor.RED + "Autotuned!");
                subtitle = (ChatColor.RED + "Feel so clean like a money machine");
                break;
            case 8:
                title = (ChatColor.RED + "Dead and Buried!");
                subtitle = (ChatColor.RED + "THAT'S BEING REVISED!");
                break;
            case 9:
                title = (ChatColor.RED + "UwU Rawr~");
                subtitle = (ChatColor.RED + "Protogen nanites injected."); //Smh intellij not accepting nanite as a word
                break;
            case 10:
                title = (ChatColor.RED + "Bogos Binted?");
                subtitle = (ChatColor.RED + "Did you get those photos printed?");
                break;
            case 11:
                title = (ChatColor.RED + "New Best!");
                subtitle = (ChatColor.RED + "98%");
                break;
            case 12:
                title = (ChatColor.RED + "So close!");
                subtitle = (ChatColor.RED + "30% far!!");
                break;
            case 13:
                title = (ChatColor.RED + "Got no time");
                subtitle = (ChatColor.RED + "You really did 'got no time to live'");
                break;
            case 14:
                title = (ChatColor.RED + "Smoked!");
                subtitle = (ChatColor.RED + "'A couple breaths of this knocks 'em right out'");
                break;
            case 15:
                title = (ChatColor.RED + "Number Fifteen");
                subtitle = (ChatColor.RED + "Burger King foot lettuce"); // I will eat paint
                break;
            case 16:
                title = (ChatColor.RED + "Morbed On!");
                subtitle = (ChatColor.RED + "Nooo not the morb juices!");
                break;
            case 17:
                title = (ChatColor.RED + "CAAAARRRLLLL");
                subtitle = (ChatColor.RED + "That kills people!");
                break;
            case 18:
                title = (ChatColor.RED + "Voted Out!");
                subtitle = (ChatColor.RED + "Guys, I'm engineer, I swear!");
                break;
            case 19:
                title = (ChatColor.RED + "Cry about it.");
                subtitle = (ChatColor.RED + "waaah waaah waaah");
                break;
            case 20:
                title = (ChatColor.RED + "The heavy is dead!");
                subtitle = (ChatColor.RED + "It is good day to be not dead!");
                break;
        }
        player.sendTitle(title, subtitle, 10, 70, 20);
    }
}
