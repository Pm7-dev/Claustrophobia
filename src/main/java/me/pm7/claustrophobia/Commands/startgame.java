package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.DataManager;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class startgame implements CommandExecutor {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final FileConfiguration config = plugin.getConfig();
    private final DataManager dm = Claustrophobia.getData();

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

        // Set the game's location and border size
        Location loc = p.getLocation().getBlock().getLocation().clone();
        loc.setY(0f);
        loc.setYaw(0f);
        dm.getConfig().set("gameLocation", loc);
        config.set("borderSize", borderSize);
        plugin.saveConfig(); // game loc has to be set before walls can be set up

        // Do the border in the nether maybe
        World nether = Bukkit.getWorld(loc.getWorld().getName() + "_nether");
        if(nether != null) {
            nether.getWorldBorder().setCenter(loc.getX(), loc.getZ());
            nether.getWorldBorder().setSize(borderSize);
        }

        plugin.removeWalls(); // it also sets up the walls, trust me

        // Spread all players about (except the guy who started the server)
        for(Player plr : Bukkit.getOnlinePlayers()) {
            Nerd n = plugin.getNerd(plr.getUniqueId());
            if(n == null || plr == p) { continue; }
            n.spawn();
        }

        Bukkit.broadcastMessage(ChatColor.RED + "Claustrophobia started!");
        return true;
    }
}
