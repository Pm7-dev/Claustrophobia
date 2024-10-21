package me.pm7.minihardcore;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardManager implements Listener {
    MiniHardcore plugin = MiniHardcore.getPlugin();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        plugin.loadScoreBoard(p);
    }


}
