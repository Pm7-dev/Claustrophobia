package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class startgame implements CommandExecutor {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Make sure the player has permission and isn't using the console
        if(!(sender instanceof Player p)) { sender.sendMessage(ChatColor.RED + "You must start the game as a player."); return true; }
        if(!p.isOp()) { sender.sendMessage(ChatColor.RED + "Player must be an operator."); return true; }

        // Figure out the border size (default to 100)
        double borderSize = 100;
        if(args.length > 0) {
            try { borderSize = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Border size must be a positive number");
                return true;
            }
            if(borderSize < 0) {borderSize *= -1;}
        }

        // Set the game's location
        Location loc = p.getLocation().getBlock().getLocation().clone();
        loc.setY(0f);
        loc.setYaw(0f);
        config.set("gameLocation", loc);

        // Set the border size and start the walls
        config.set("borderSize", borderSize);
        plugin.setupWalls();

        plugin.saveConfig();

        // Spread all players about
        for(Player plr : Bukkit.getOnlinePlayers()) {
            double x = (loc.getX() + (Math.random() * borderSize) - (borderSize /2) - 1);
            double z = (loc.getZ() + (Math.random() * borderSize) - (borderSize /2) - 1);
            plr.teleport(loc.getWorld().getHighestBlockAt((int) x, (int) z).getLocation().add(0, 2, 0));
        }
        return true;
    }
}
