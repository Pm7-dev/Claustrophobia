package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class reviveplayer implements CommandExecutor {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        Nerd n = plugin.getNerd(p.getUniqueId());
        n.revive();
        return true;
    }
}
