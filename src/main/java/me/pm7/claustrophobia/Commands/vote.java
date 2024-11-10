package me.pm7.claustrophobia.Commands;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.DataManager;
import me.pm7.claustrophobia.Objects.Nerd;
import me.pm7.claustrophobia.Objects.VoteMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/*

    This file manages everything to do with the voting system. I don't know if it will be a jumbled mess or not because
    I haven't written it yet. If anything in this code will be a jumbled mess, it will probably be this.
    oki coding time now uwu

 */

public class vote implements CommandExecutor, Listener {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final DataManager dm = Claustrophobia.getData();
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) { return true; }
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { return true; }
        if(nerd.isDead()) {p.sendMessage(ChatColor.RED + "Only living players can vote"); return true;}

        // endgame
        if(dm.getConfig().getBoolean("endgame")) {
            p.sendMessage(ChatColor.RED + "You cannot save anyone now.");
            return true;
        }

        // All of this is just for determining the size of the inventory lol
        int deadPlayers = 0;
        for(Nerd n : plugin.getNerds()) { if(n.isVotable()) { deadPlayers++; } }
        if(deadPlayers == 0) {p.sendMessage(ChatColor.RED + "Nobody is votable"); return true;} // why

        sendVoteMenu(p);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if(!(inv.getHolder() instanceof VoteMenuHolder)) {return;}
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        Nerd n = plugin.getNerd(p.getUniqueId());
        if(n == null) { return; }

        ItemStack item = e.getCurrentItem();
        if(item == null) {return;}
        ItemMeta meta = item.getItemMeta();
        String[] words = meta.getItemName().split(" ");
        String name = words[words.length-1];
        Nerd clicked = plugin.getNerd(name);
        if(clicked == null) {
            p.closeInventory();
            p.sendMessage(ChatColor.RED + "There is not a player with that name");
            return;
        }

        // Actually handle the voting
        boolean revived = false;
        if(item.getType() == Material.SLIME_BALL) {
            int success = clicked.addVote(n.getUuid());
            if(success >= 0) { p.sendMessage(ChatColor.GREEN + "Successfully voted for " + clicked.getName()); }
            else { p.sendMessage(ChatColor.RED + "There was an error with voting! Try reopening the menu"); }

            // addVote() returns -1 if failed, 0 if succeeded, and 1 if this vote revives the player
            if(success == 1) {revived = true;}
        } else if (item.getType() == Material.FIRE_CHARGE) {
            boolean success = clicked.removeVote(n.getUuid());
            if(success) { p.sendMessage(ChatColor.GREEN + "Successfully removed vote from " + clicked.getName()); }
            else { p.sendMessage(ChatColor.RED + "There was an error with vote removal! Try reopening the menu"); }
        } else { return; }

        // play funny click sound
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);

        // If the player gets revived from this, reload the voting menu to not include that player
        if(revived) {
            sendVoteMenu(p);
        } else {
            // Otherwise just switch the button
            ItemStack voteButton = new ItemStack(Material.SLIME_BALL);
            ItemMeta vbMeta = voteButton.getItemMeta();
            if (clicked.getVotes().contains(n.getUuid().toString())) {
                voteButton.setType(Material.FIRE_CHARGE);
                vbMeta.setItemName(ChatColor.RED + "Click to remove vote from " + clicked.getName());
            } else {
                vbMeta.setItemName(ChatColor.GREEN + "Click to vote " + clicked.getName());
            }
            voteButton.setItemMeta(vbMeta);
            inv.setItem(e.getSlot(), voteButton);

            // Update head
            ItemStack head = getPlayerHead(clicked);
            inv.setItem(e.getSlot()-9, head);
        }
    }

    public void sendVoteMenu(Player p) {
        p.closeInventory();

        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { return; }

        // All of this is just for determining the size of the inventory lol
        int deadPlayers = 0;
        for(Nerd n : plugin.getNerds()) { if(n.isVotable()) { deadPlayers++; } }
        if(deadPlayers == 0) {p.sendMessage(ChatColor.RED + "There are no players up for vote"); return;} // why

        int invSize = 0;
        while(deadPlayers > 0) { // Must be two rows per 9 dead players
            invSize += 18;
            deadPlayers -=9;
        }
        if(invSize > 54) {invSize = 54;} // Inventory size can't be over 54 without having weird bugs
        Inventory inv = Bukkit.createInventory(new VoteMenuHolder(), invSize, "Vote Menu");

        // Now that I've actually figured out the inventory's size, it's time to start adding the player heads
        int index = 0;
        for(Nerd n : plugin.getNerds()) {
            if(n.isVotable()) {

                // Set the current index with the player's head
                inv.setItem(index, getPlayerHead(n));

                // Set the item below the head to a vote button
                ItemStack voteButton = new ItemStack(Material.SLIME_BALL);
                ItemMeta vbMeta = voteButton.getItemMeta();
                if(n.getVotes().contains(nerd.getUuid().toString())) {
                    voteButton.setType(Material.FIRE_CHARGE);
                    vbMeta.setItemName(ChatColor.RED + "Click to remove vote from " + n.getName());
                } else {
                    vbMeta.setItemName(ChatColor.GREEN + "Click to vote " + n.getName());
                }
                voteButton.setItemMeta(vbMeta);
                inv.setItem(index + 9, voteButton);

                index++;
                if(index == 9 || index == 27 || index == 45) { index += 9; } // Don't let it write to the button rows
            }
        }

        // Finally, open the inventory for the player that requested it
        p.openInventory(inv);
    }

    public ItemStack getPlayerHead(Nerd n) {
        PlayerProfile profile = Bukkit.createPlayerProfile(n.getUuid());

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwnerProfile(profile);
        meta.setItemName(n.getName());
        meta.setRarity(ItemRarity.RARE);

        // could probably be optimized a LOT, but this really shouldn't matter
        int neededVotes = 0;
        for(Nerd nerd : plugin.getNerds()) { if(!nerd.isDead()) {neededVotes++;} }
        neededVotes = (int) (neededVotes *  ((float) config.getInt("reviveVotePercentage")/100));
        if(neededVotes == 0) {neededVotes++;}
        meta.setLore(Collections.singletonList(n.getVotes().size() + "/" + neededVotes + " votes"));
        item.setItemMeta(meta);
        return item;
    }
}
