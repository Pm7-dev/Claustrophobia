package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Connections implements Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // Warn the player about render distance struggles
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(p.getClientViewDistance() <= 3) {
                p.sendMessage(ChatColor.RED + "Warning: This plugin requires a render distance of at least 4 to function properly with a 100x100 border (higher if larger). A minimum of 7 render distance is recommended.");
            } else if (p.getClientViewDistance() <= 6) {
                p.sendMessage(ChatColor.RED + "Warning: For the best experience this plugin recommends a render distance of at least 7 with a 100x100 border (higher if larger).");
            }
        }, 10L);


        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd != null) {
            // Check for a name change
            if(!p.getName().equals(nerd.getName())) {
                nerd.setName(p.getName());
            }
        } else {
            // Add a new nerd if the nerd data has not been done
            nerd = new Nerd(p.getUniqueId());
            plugin.getNerds().add(nerd);

            //TODO: Maybe put a welcome message here or something
        }

        if(nerd.isRevived()) { nerd.revive(); }

        // Dead player stuff
        if(nerd.isDead()) {
            p.setGameMode(GameMode.ADVENTURE);
            p.setHealth(20.0d);
            p.setFoodLevel(20);
            p.setInvisible(true);
            p.setAllowFlight(true);
            p.setCollidable(false);
            p.setInvulnerable(true);
            p.setFlying(true);
            for (Player plr : Bukkit.getOnlinePlayers()) {if(nerd.isDead()) { plr.hidePlayer(plugin, p); } }
        }

        // Hide other dead players
        for(Player plr : Bukkit.getOnlinePlayers()) {
            Nerd plrNerd = plugin.getNerd(plr.getUniqueId());
            if(plrNerd != null && plrNerd.isDead()) { p.hidePlayer(plugin, plr); }
        }

    }
}
