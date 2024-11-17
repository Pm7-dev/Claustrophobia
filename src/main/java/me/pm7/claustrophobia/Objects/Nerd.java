package me.pm7.claustrophobia.Objects;

import me.pm7.claustrophobia.Claustrophobia;
import me.pm7.claustrophobia.DataManager;
import me.pm7.claustrophobia.Listeners.SpectatorManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class Nerd implements ConfigurationSerializable {
    private static final Claustrophobia plugin = Claustrophobia.getPlugin();
    private static final DataManager dm = Claustrophobia.getData();
    private final FileConfiguration config = plugin.getConfig();

    private static final String[] deathMessages = { "GET OUT!", "Smoked", "Smack!", "Blam!", "Oof...", "That's gotta hurt.",
        "Socked!", "Drat!", "Flabbergasted", "Defenestrated!", "So close...", "If only!", "Imagine dying...", "Plundered",
        "sina moli a!", "Incorrect move.", "Try again", "Insert Coin", "What a loser", "Juiced", "Continue?", "Scram!",
        "Bamboozled", "Foiled again", "...huh?", "Refund!", "Cry about it", "Not fair >:(", "Wat.", "oops.", ":(",
        "Maybe next time", "Consider not dying", "Thanks, Obama", "D-E-D, Dead!", "Oh naurr", "dang.", "rawr~", "Good night!",
        "You lost the game", "Sad trombone noise", "Bwomp.", "*Vine boom noise*", "F", "Aw shucks", "You Died!", "Joever",
        "Soiled!", "Spoiled!", "Meow :3", "Dumb idiot", "Blockhead!", "Silly"
    };
    private static final String[] finalDeathMessages = { "Farewell.", "Goodbye.", "Heaven awaits you.", "No longer.",
        "Be gone.", "It ends here.", "There's nothing left.", "Such pity.", "It's over.", "Your final breath."
    };

    private String name;
    private UUID uuid;
    private boolean dead, revived, votable, outOfBorder;
    private long deadMinutes;
    private List<String> votes;

    public Nerd(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if(p != null) {
            this.name = p.getName();
            this.uuid = uuid;
            this.dead = false;
            this.revived = false;
            this.votable = false;
            this.outOfBorder = false;
            this.deadMinutes = 0;
            this.votes = new ArrayList<>();
        } else {
            plugin.getLogger().warning("Error while creating nerd!");
        }
    }

    public Nerd(String name, UUID uuid, boolean dead, boolean revived, boolean votable, boolean outOfBorder, long deadMinutes, List<String> votes) {
        this.name = name;
        this.uuid = uuid;
        this.dead = dead;
        this.revived = revived;
        this.votable = votable;
        this.outOfBorder = outOfBorder;
        this.deadMinutes = deadMinutes;
        this.votes = votes;
    }

    public void setName(String newName) { name = newName; }
    public String getName() { return name; }
    public UUID getUuid() {return uuid;}
    public boolean isDead() { return dead; }
    public boolean isRevived() { return revived; }
    public boolean isVotable() { return votable; }
    public boolean isOutOfBorder() { return outOfBorder; }
    public void setOutOfBorder(boolean newB) { outOfBorder = newB; }
    public long getMinutes() { return deadMinutes; }
    public List<String> getVotes() {return votes;}

    public void addMinute() {
        deadMinutes += 1;
        if(deadMinutes >= config.getLong("deathTime")) {
            votable = true;
            deadMinutes = 0;

            // Announce the vote to everyone
            for (Player p : Bukkit.getOnlinePlayers()) {
                if(p.getUniqueId() == uuid) { p.sendMessage(ChatColor.YELLOW + "You are now up for vote!"); }
                else { p.sendMessage(ChatColor.YELLOW + name + " is now up for vote!"); }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
            }

        }
    }

    public void kill() {
        dead = true;
        revived = false;
        votable = false;

        // I don't know why the player might be offline, but just in case, don't do anything
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) { return; }

        // Drop the player's inventory if keep inv is off
        World world = p.getWorld();
        if(world.getGameRuleValue(GameRule.KEEP_INVENTORY) == Boolean.FALSE) {
            Inventory inv = p.getInventory();
            Location loc = p.getLocation();
            double power = 0.2D;
            for(ItemStack item : inv.getContents()) {
                if(item != null && item.getItemMeta() != null) {
                    double xVel = -power + (Math.random() * (power*2));
                    double zVel = -power + (Math.random() * (power*2));
                    Entity dropped = world.dropItem(loc, item);
                    dropped.setVelocity(new Vector(xVel, 0.3, zVel));
                }
            }
            inv.clear();
        }

        // Stop the player's interaction with anything
        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(20.0d);
        p.setFoodLevel(20);
        p.setAllowFlight(true);
        p.setCollidable(false);
        p.setInvulnerable(true);
        p.setInvisible(true);
        p.setFlying(true);

        // Show dead players and hide player from alive players
        for(Player plr : Bukkit.getOnlinePlayers()) {
            Nerd plrNerd = plugin.getNerd(plr.getUniqueId());
            if (plrNerd != null && plrNerd.isDead()) {
                p.showPlayer(plugin, plr);
            } else {
                plr.hidePlayer(plugin, p);
            }
        }

        // Add them to the dead player team
        SpectatorManager.addPlayerToDeathScoreboard(p);

        // Add a little upwards velocity
        Vector v = p.getVelocity().add(new Vector(0, 0.8, 0));
        p.setVelocity(v);

        // Remove all player potion effects (in case they have the darkness of the border)
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(PotionEffect pe : p.getActivePotionEffects()) { p.removePotionEffect(pe.getType()); }
        }, 10L);

        if(dm.getConfig().getBoolean("endgame")) {
            p.sendTitle(ChatColor.RED + finalDeathMessages[(int) (Math.random() * finalDeathMessages.length)], ChatColor.RED + "You will not respawn.", 10, 80, 30);

            // Check for a winner
            int count = 0;
            Nerd winner = null;
            for(Nerd n : plugin.getNerds()) {
                if(!n.isDead()) {
                    count++;
                    winner = n;
                }
            }
            if(count == 1) {
                Player win = Bukkit.getPlayer(winner.getUuid());
                if(win != null) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        win.sendMessage(ChatColor.YELLOW + "So, it's just you, huh.");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> win.sendMessage(ChatColor.YELLOW + "Enjoy the rest of your life, I guess."), 40L);
                    }, 45L);
                }
            }
        } else {
            // Sending a fun little death message
            long waitMinutes = config.getLong("deathTime");
            if (waitMinutes == 0) {
                p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], "", 10, 70, 20);
                votable = true; // If there is no wait, send them to the voting right away
            } else if (waitMinutes <= 60) {
                p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], ChatColor.RED + "You will be up for vote in " + waitMinutes + " minutes", 10, 70, 20);
            } else {
                int hours = (int) Math.floor((double) waitMinutes / 60);
                int minutes = (int) (waitMinutes % 60);
                if (minutes == 0) {
                    p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], ChatColor.RED + "You will be up for vote in " + hours + " hours", 10, 70, 20);
                } else {
                    p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], ChatColor.RED + "You will be up for vote in " + hours + " hours and " + minutes + " minutes", 10, 70, 20);
                }
            }
        }

        // Count the new amount of needed votes to revive in case it changed from this death
        int neededVotes = 0;
        for(Nerd count : plugin.getNerds()) { if(!count.isDead()) {neededVotes++;} }
        neededVotes = (int) (neededVotes *  ((float) config.getInt("reviveVotePercentage")/100));
        if(neededVotes == 0) {neededVotes++;}

        for(Nerd nerd : plugin.getNerds()) {
            nerd.getVotes().remove(uuid.toString()); // Since this player is dead, their vote does not count
            if(nerd.getVotes().size() >= neededVotes) { nerd.revive(); } // In case the death lowered the required amount of votes
        }

        plugin.savePlayerData(); // Probably important enough of a change to require this
    }

    public void revive() {

        // If the player is offline, just set them to be revived once they are back online
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            revived = true;
            return;
        }

        // puts the player at a random safe spot on the map
        spawn();

        // Revived players will no longer be dead, and should not be votable anymore
        dead = false;
        deadMinutes = 0;
        votable = false;
        votes = new ArrayList<>();

        outOfBorder = false;

        // Let them know what happened
        p.sendTitle(ChatColor.GREEN + "You have been revived!", "", 10, 70, 20);

        // Let the world know what happened
        for (Player plr : Bukkit.getOnlinePlayers()) {
            plr.sendMessage(ChatColor.YELLOW + name + " has been revived!");
            plr.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
        }

        // Allow them to interact with the world again
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(false);
        p.setHealth(20.0d);
        p.setFoodLevel(20);
        p.setCollidable(true);
        p.setInvulnerable(false);
        p.setInvisible(false);
        p.setFireTicks(0);
        p.setFreezeTicks(0);

        // Remove them from the dead player team
        SpectatorManager.removePlayerFromDeathScoreboard(p);

        // Remove all player potion effects (in case they have the darkness of the border for some reason)
        for(PotionEffect pe : p.getActivePotionEffects()) { p.removePotionEffect(pe.getType()); }

        // Hide dead players and show player to other alive players
        for(Player plr : Bukkit.getOnlinePlayers()) {
            Nerd plrNerd = plugin.getNerd(plr.getUniqueId());
            if (plrNerd != null && plrNerd.isDead()) {
                p.hidePlayer(plugin, plr);
            } else {
                plr.showPlayer(plugin, p);
            }
        }

        plugin.savePlayerData(); // Probably important enough of a change to require this
    }

    public short addVote(UUID uuid) {
        if(isVotable() && !votes.contains(uuid.toString())) {
            votes.add(uuid.toString());

            int neededVotes = 0;
            for(Nerd nerd : plugin.getNerds()) { if(!nerd.isDead()) {neededVotes++;} }
            neededVotes = (int) (neededVotes *  ((float) config.getInt("reviveVotePercentage")/100));
            if(neededVotes == 0) {neededVotes++;}
            if(votes.size() >= neededVotes) {
                revive();
                return 1;
            }

            return 0;
        }
        return -1;
    }

    public boolean removeVote(UUID uuid) {
        if(isVotable() && votes.contains(uuid.toString())) {
            votes.remove(uuid.toString());
            return true;
        }
        return false;
    }

    public void spawn() {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) { return; }

        Location gameLoc = dm.getConfig().getLocation("gameLocation");
        if(gameLoc == null || gameLoc.getWorld() == null) {return;}

        Location loc = gameLoc.clone();
        double borderSize = config.getDouble("borderSize");
        double x = (loc.getX() + (Math.random() * (borderSize - 4)) - (borderSize /2));
        double z = (loc.getZ() + (Math.random() * (borderSize - 4)) - (borderSize /2));

        Block ground = loc.getWorld().getHighestBlockAt((int) x, (int) z);
        while(!ground.getType().isSolid() && ground.getType() != Material.WATER) { // water sources are really easy
            x = (loc.getX() + (Math.random() * (borderSize - 4)) - (borderSize/2));
            z = (loc.getZ() + (Math.random() * (borderSize - 4)) - (borderSize/2));
            ground = loc.getWorld().getHighestBlockAt((int) x, (int) z);
        }

        Location tpLoc = ground.getLocation().add(0, 2, 0);
        tpLoc.setYaw(((float) Math.random()*360) - 180);
        p.teleport(tpLoc);
    }

    // How we load player data from config stuff
    public static Nerd deserialize(ConfigurationSection serializedData) {
        if (serializedData == null) {
            plugin.getLogger().log(Level.WARNING, "Serialized data is null! Cannot load Nerd");
            return null;
        }

        String name = serializedData.getString("name");
        UUID uuid = UUID.fromString(Objects.requireNonNull(serializedData.getString("uuid")));
        boolean dead = serializedData.getBoolean("dead");
        boolean revived = serializedData.getBoolean("revived");
        boolean votable = serializedData.getBoolean("votable");
        boolean outOfBorder = serializedData.getBoolean("outOfBorder");
        long deadMinutes  = serializedData.getLong("deadMinutes");
        List<String> votes = serializedData.getStringList("votes");

        return new Nerd(name, uuid, dead, revived, votable, outOfBorder, deadMinutes, votes);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedData = new HashMap<>();
        serializedData.put("name", this.name);
        serializedData.put("uuid", this.uuid.toString());
        serializedData.put("dead", this.dead);
        serializedData.put("votable", this.votable); //.valueOf to get back
        serializedData.put("outOfBorder", this.outOfBorder); //.valueOf to get back
        serializedData.put("deadMinutes", this.deadMinutes);
        serializedData.put("votes", this.votes);
        return serializedData;
    }
}
