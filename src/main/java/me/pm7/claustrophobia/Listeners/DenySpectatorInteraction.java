package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

// Funny class that blocks the spectators from actually doing anything.

public class DenySpectatorInteraction implements Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

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
    public void onPlayerInteract(EntityInteractEvent e) {
        Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
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
}
