package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class reviveplayer implements CommandExecutor {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) { return true; }
        if(!p.isOp()) {p.sendMessage(ChatColor.RED + "Only operators can run this command"); return true;}
        if(args.length == 0) {
            Nerd n = plugin.getNerd(p.getUniqueId());
            if(n != null) { n.revive(); }
        } else {
            Nerd n = plugin.getNerd(args[0]);
            if(n != null) {
                p.sendMessage(ChatColor.GREEN + "Successfully revived " + n.getName());
                n.revive();
            }
            else {
                p.sendMessage(ChatColor.RED + "Could not find player " + args[0]);
            }
        }

        return true;
    }
}
