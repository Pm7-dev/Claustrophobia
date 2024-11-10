package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.DataManager;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Border implements Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final DataManager dm = Claustrophobia.getData();

    // Checking if the player is outside the border
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Nerd n = plugin.getNerd(p.getUniqueId());
        if(n == null) { return; }
        Location loc = dm.getConfig().getLocation("gameLocation");
        if(loc == null) {return;}
        double borderSize = plugin.getConfig().getDouble("borderSize");
        Location pLoc = p.getLocation();

        // Teleport any non-op spectators that get a bit too far away
        if(n.isDead()) {
            if(p.isOp()) { return; } // OP players are exempt from this harsh border treatment
            if(n.isOutOfBorder()) {
                if(Math.abs(loc.getX()-pLoc.getX()) > borderSize || Math.abs(loc.getZ()-pLoc.getZ()) > borderSize) {
                    p.sendMessage(ChatColor.RED + "You are getting too far away from the border!");
                    Location tpBack = loc.clone();
                    tpBack.setY(pLoc.getY());
                    tpBack.setPitch(pLoc.getPitch());
                    tpBack.setYaw(pLoc.getYaw());
                    p.teleport(tpBack);
                }
            }
        }

        // Detect when players are outside of the border to damage/teleport them back
        if(Math.abs(loc.getX()-pLoc.getX()) - (borderSize/2) > 0 || Math.abs(loc.getZ()-pLoc.getZ()) - (borderSize/2) > 0) {
            n.setOutOfBorder(true);
        } else {
            n.setOutOfBorder(false);
        }
    }

    // Prevent mobs from spawning outside the border
    @EventHandler
    public void onMobSpawn(EntitySpawnEvent e) {



        Entity entity = e.getEntity();
        if(e.getEntity().getType() == EntityType.ITEM) {return;} // Forgot that items count lol

        Location loc = dm.getConfig().getLocation("gameLocation");
        if(loc == null) {return;}
        double borderSize = plugin.getConfig().getDouble("borderSize");
        Location pLoc = entity.getLocation();
        if(Math.abs(loc.getX()-pLoc.getX()) - (borderSize/2) > 0 || Math.abs(loc.getZ()-pLoc.getZ()) - (borderSize/2) > 0) {
            e.setCancelled(true);
        }
    }
}
