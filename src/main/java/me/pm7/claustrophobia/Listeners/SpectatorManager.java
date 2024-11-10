package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

// Funny class that blocks the spectators from actually doing anything.

public class SpectatorManager implements Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    // Scoreboard team stuff for when we want to make translucent players
    private static final org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
    private static Scoreboard board;
    private static Team team;

    public static void addPlayerToScoreboard(Player p) {
        if(team == null) {
            board = manager.getNewScoreboard();
            team = board.registerNewTeam("spectator");
            team.setCanSeeFriendlyInvisibles(true);
            team.setColor(ChatColor.GRAY);
        }
        p.setScoreboard(board);
        team.addEntry(p.getName());
    }

    public static void removePlayerFromScoreboard(Player p) {
        if(team == null) { return; }
        p.setScoreboard(board);
        team.removeEntry(p.getName());
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return; }
        if(nerd.isDead()) {
            addPlayerToScoreboard(e.getPlayer());
        } else {
            removePlayerFromScoreboard(e.getPlayer());
        }
    }

    // Force spectators to fly
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Nerd n = plugin.getNerd(p.getUniqueId());
        if(n==null || !n.isDead()) {return;}
        if(!p.isFlying()) { p.setFlying(true); }
    }






    // Onslaught of events that stop the spectators from messing about
    @EventHandler
    public void onPlayerPunch(EntityDamageByEntityEvent e) {
        Nerd nerd = plugin.getNerd(e.getDamager().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e) {
        Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Nerd nerd = plugin.getNerd(e.getWhoClicked().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent e) {
        Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerMount(PlayerInteractEntityEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerInventoryInteract(InventoryInteractEvent e) {
        Nerd nerd = plugin.getNerd(e.getWhoClicked().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerProjectileLaunch(ProjectileLaunchEvent e) {
        Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null) {return;}
        if (nerd.isDead()) {
            e.setCancelled(true);
        }
    }
}
