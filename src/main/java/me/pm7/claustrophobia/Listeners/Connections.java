package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.DataManager;
import me.pm7.claustrophobia.Objects.Nerd;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Connections implements Listener {
    private static final Claustrophobia plugin = Claustrophobia.getPlugin();
    private static final DataManager dm = Claustrophobia.getData();

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // Warn the player about render distance issues
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(p.getClientViewDistance() <= 3) {
                p.sendMessage(ChatColor.RED + "Warning: This plugin requires a render distance of at least 4 to function properly with a 100x100 border (higher if larger). A minimum of 7 render distance is recommended.");
                p.playSound(p, Sound.BLOCK_ANVIL_LAND, 500, 1.6f);
            } else if (p.getClientViewDistance() <= 6) {
                p.sendMessage(ChatColor.RED + "Warning: For the best experience this plugin recommends a render distance of at least 7 with a 100x100 border (higher if larger).");
                p.playSound(p, Sound.BLOCK_ANVIL_LAND, 500, 1.6f);
            }
        }, 50L);

        checkPlayerDataOnLoad(p);
    }

    public static void checkPlayerDataOnLoad(Player p) {
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd != null) {
            // Check for a name change
            if(!p.getName().equals(nerd.getName())) {
                nerd.setName(p.getName());
                plugin.savePlayerData();
            }
        } else {
            // Add a new nerd if the nerd data has not been done
            nerd = new Nerd(p.getUniqueId());
            plugin.getNerds().add(nerd);
            nerd.spawn(); // put new player at a random spot on the map

            // send player a thing to prompt to tell them how to play
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                BaseComponent[] component = new ComponentBuilder()
                        .append("Welcome to Claustrophobia! Click this message to learn how to play")
                        .color(ChatColor.GREEN.asBungee()).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claustrophobiainfo"))
                        .create();
                p.spigot().sendMessage(component);
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
            }, 20L);

            plugin.savePlayerData();
        }

        if(nerd.isRevived()) { nerd.revive(); }

        // Dead player stuff
        if(nerd.isDead()) {
            p.setGameMode(GameMode.ADVENTURE);
            p.setHealth(20.0d);
            p.setFoodLevel(20);
            p.setAllowFlight(true);
            p.setCollidable(false);
            p.setInvulnerable(true);
            p.setInvisible(true);
            p.setFlying(true);
            for (Player plr : Bukkit.getOnlinePlayers()) {
                Nerd n = plugin.getNerd(plr.getUniqueId());
                if(n != null && !n.isDead()) {
                    plr.hidePlayer(plugin, p);
                }
            }
        } else {
            if(dm.getConfig().getBoolean("endgame")) {
                p.sendMessage(ChatColor.YELLOW + "The game is nearing its end. Let this be a reminder that this is your final life.");
            }

            // Hide dead players
            for(Player plr : Bukkit.getOnlinePlayers()) {
                Nerd plrNerd = plugin.getNerd(plr.getUniqueId());
                if (plrNerd != null && plrNerd.isDead()) {
                    p.hidePlayer(plugin, plr);
                }
            }

            p.setAllowFlight(false);
            p.setCollidable(true);
            p.setInvulnerable(false);
            p.setInvisible(false);
        }
    }
}
