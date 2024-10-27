package me.pm7.claustrophobia.Listeners;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Death implements Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player p)) { return; }

        if(p.getHealth() - e.getFinalDamage() <= 0.0d) {
            Nerd nerd = plugin.getNerd(p.getUniqueId());
            if(nerd == null) { return; }
            e.setCancelled(true);
            nerd.kill();

            Bukkit.broadcastMessage(ChatColor.RED + p.getName() + " has died!");
            for(Player plr : Bukkit.getOnlinePlayers()) { plr.getWorld().playSound(plr, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 999, 1.0f); }
            p.playSound(p, Sound.ENTITY_PLAYER_DEATH, 999, 1.0f);
        }
    }
}
