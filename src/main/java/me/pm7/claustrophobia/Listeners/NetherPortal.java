package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class NetherPortal implements Listener {
    private static final Claustrophobia plugin = Claustrophobia.getPlugin();

    @EventHandler
    public void onPlayerExitPortal(PlayerPortalEvent e) {
        System.out.println("aaaaaa");
        World w = e.getTo().getWorld();
        if(w == null) { return; }
        World.Environment env = w.getEnvironment();
        if(env == World.Environment.NORMAL || env == World.Environment.CUSTOM) { // idk about custom but
            Nerd n = plugin.getNerd(e.getPlayer().getUniqueId());
            if(n == null) { return; }

            System.out.println("aaa");

            e.setCancelled(true);
            n.spawn();
        }
    }
}
