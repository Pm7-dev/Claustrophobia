package me.pm7.minihardcore.Listeners;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;

import static me.pm7.minihardcore.MiniHardcore.started;

public class PlayerJoinListener implements Listener {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(started) {
            HashSet<String> dead = plugin.getDead();
            Player p = e.getPlayer();
            if(dead.contains(p.getName())) {
                plugin.checkRevive(e.getPlayer());
            }
        }
    }
}
