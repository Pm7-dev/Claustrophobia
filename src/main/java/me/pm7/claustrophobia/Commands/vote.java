package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/*

    This file manages everything to do with the voting system. I don't know if it will be a jumbled mess or not because
    I haven't written it yet. If anything in this code will be a jumbled mess, it will probably be this.
    oki coding time now uwu

 */

public class vote implements CommandExecutor, Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) { return true; }
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { return true; }
        if(nerd.isDead()) {p.sendMessage(ChatColor.RED + "Only living players can vote"); return true;}

        // All of this is just for determining the size of the inventory lol
        int deadPlayers = 0;
        for(Nerd n : plugin.getNerds()) { if(n.isDead()) { deadPlayers++; } }
        if(deadPlayers == 0) {p.sendMessage(ChatColor.RED + "Nobody is dead yet lol"); return true;} // why

        int invSize = 0;
        while(deadPlayers > 0) { // Must be two rows per 9 dead players
            invSize += 18;
            deadPlayers -=9;
        }
        if(invSize > 54) {invSize = 54;} // Inventory size can't be over 54 without having weird bugs
        Inventory inv = Bukkit.createInventory(p, invSize);

        // Now that I've actually figured out the inventory's size, it's time to start adding the player heads
        for(Nerd n : plugin.getNerds()) {
            if(n.isDead()) {

            }
        }


        // Finally, open the inventory for the player that requested it
        p.openInventory(inv);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

    }
}
