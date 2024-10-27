package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Movement implements Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    // Checking if the player is outside the border
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Nerd n = plugin.getNerd(p.getUniqueId());
        if(n == null) { return; }
        if(n.isDead()) { return; }


        Location loc = plugin.getConfig().getLocation("gameLocation");
        if(loc == null) {return;}
        double borderSize = plugin.getConfig().getDouble("borderSize");

        Location pLoc = p.getLocation();
        if(Math.abs(loc.getX()-pLoc.getX()) - (borderSize/2) > 0 || Math.abs(loc.getZ()-pLoc.getZ()) - (borderSize/2) > 0) {
            n.setOutOfBorder(true);
        } else {
            n.setOutOfBorder(false);
        }
    }
}
