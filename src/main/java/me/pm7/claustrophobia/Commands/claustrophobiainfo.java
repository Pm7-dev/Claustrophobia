package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class claustrophobiainfo implements CommandExecutor {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "How to play Claustrophobia:");
        sender.sendMessage(ChatColor.GOLD + "1. Don't die");
        sender.sendMessage(ChatColor.GOLD + "2. Make alliances");
        sender.sendMessage(ChatColor.GOLD + "3. Cause mischief");
        sender.sendMessage(ChatColor.GOLD + "4. If one of your friends die, use /votemenu to revive them");
        sender.sendMessage(ChatColor.GOLD + "5. If one of your enemies die, don't use /votemenu to revive them");
        sender.sendMessage(ChatColor.GOLD + "6. If you die, hope you have allies that will revive you");

        String time;
        long waitMinutes = config.getLong("deathTime");
        int hours = (int) (waitMinutes / 60);
        int minutes = (int) (waitMinutes % 60);
        if(hours == 1) {time = hours + " hour";} else {time = hours + " hours";}
        if (minutes != 0) {
            if (minutes == 1) {time += " and 1 minute";} else {time += " and " + minutes + " minutes";}
        }
        sender.sendMessage(ChatColor.GRAY + " If you die, you will have to wait " + time + ", after which you will be put up for vote. Once " + config.getInt("reviveVotePercentage") + "% of players have voted for your revival, you will be able to respawn. You can vote/unvote players with the /votemenu command. Click the slimeball to vote a player, and the fire charge to unvote a player.");
        return true;
    }
}
