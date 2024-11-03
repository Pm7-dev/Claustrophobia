package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class endgame implements CommandExecutor {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player p)) { return true; }
        if(!p.isOp()) {p.sendMessage(ChatColor.RED + "Only operators can run this command"); return true;}

        for(Player plr : Bukkit.getOnlinePlayers()) { plr.getWorld().playSound(plr, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 999, 1.0f); }
        Bukkit.broadcastMessage(ChatColor.YELLOW + "It's time to end things. The revive system has been disabled.");

        plugin.getConfig().set("endgame", true);
        plugin.saveConfig();
        return true;
    }
}
