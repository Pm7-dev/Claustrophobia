package me.pm7.minihardcore.Listeners;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashSet;

public class DenySpectatorInteraction implements Listener {
    MiniHardcore plugin = MiniHardcore.getPlugin();

    @EventHandler
    public void onPlayerBreakBLock(BlockBreakEvent e) {
        HashSet<String> dead = plugin.getDead();
        Player p = e.getPlayer();
        if(dead.contains(p.getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerPunch(EntityDamageByEntityEvent e) {
        HashSet<String> dead = plugin.getDead();
        Entity entity = e.getDamager();
        if(entity instanceof Player) {
            Player p = (Player) entity;
            if(dead.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e) {
        HashSet<String> dead = plugin.getDead();
        Entity entity = e.getEntity();
        if(entity instanceof Player) {
            Player p = (Player) entity;
            if (dead.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        HashSet<String> dead = plugin.getDead();
        Player p = e.getPlayer();
        if (dead.contains(p.getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        HashSet<String> dead = plugin.getDead();
        HumanEntity humanEntity = e.getWhoClicked();
        Player p = Bukkit.getPlayer(humanEntity.getName());
        if (dead.contains(p.getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        HashSet<String> dead = plugin.getDead();
        Entity entity = e.getEntity();
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (dead.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent e) {
        HashSet<String> dead = plugin.getDead();
        Entity entity = e.getEntity();
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (dead.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(EntityInteractEvent e) {
        HashSet<String> dead = plugin.getDead();
        Entity entity = e.getEntity();
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (dead.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e) {
        HashSet<String> dead = plugin.getDead();
        Player p = e.getPlayer();
        if(dead.contains(p.getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerMount(PlayerInteractEntityEvent e) {
        HashSet<String> dead = plugin.getDead();
        Player p = e.getPlayer();
        if (dead.contains(p.getName())) {
            e.setCancelled(true);
        }
    }
}
