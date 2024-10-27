package me.pm7.claustrophobia.Objects;

import me.pm7.claustrophobia.Claustrophobia;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Nerd implements ConfigurationSerializable {
    private final Claustrophobia plugin = Claustrophobia.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    private static final String[] deathMessages = { "GET OUT!", "Smoked", "Smack!", "Blam!", "Oof...", "That's gotta hurt.",
        "Socked!", "Drat!", "Flabbergasted", "Defenestrated!", "So close...", "If only!", "Imagine dying...", "Plundered",
        "sina moli a!", "Incorrect move.", "Try again", "Insert Coin", "What a loser", "Juiced", "Continue?", "Scram!",
        "Bamboozled", "Foiled again", "...huh?", "Refund!", "Cry about it", "Not fair >:(", "Wat.", "oops.", ":(",
        "Maybe next time", "Consider not dying", "Thanks, Obama", "D-E-D, Dead!", "Oh naurr", "dang.", "rawr~", "Good night.",
        "You lost the game", "Sad trombone noise", "Bwomp.", "*Vine boom noise*", "F", "Aw shucks", "You Died!", "Joever",
        "Soiled!", "Spoiled!", "Meow :3"
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

        // Play a cool little effect :D
        p.getWorld().strikeLightningEffect(p.getLocation());

        // Stop the player's interaction with anything
        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(20.0d);
        p.setFoodLevel(20);
        p.setInvisible(true);
        p.setAllowFlight(true);
        p.setCollidable(false);
        p.setInvulnerable(true);
        p.setFlying(true);
        for (Player plr : Bukkit.getOnlinePlayers()) { plr.hidePlayer(plugin, p); }

        // Remove all player potion effects (in case they have the darkness of the border)
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(PotionEffect pe : p.getActivePotionEffects()) { p.removePotionEffect(pe.getType()); }
        }, 10L);

        // Very overly complicated way of broadcasting a little death message
        long waitMinutes = config.getLong("deathTime");
        if(waitMinutes == 0) {
            p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], "", 10, 70, 20);
            votable = true; // If there is no wait, send them to the voting right away
        } else if (waitMinutes <= 60) {
            p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], ChatColor.RED + "You will be up for vote in " + waitMinutes + " minutes", 10, 70, 20);
        } else {
            int hours = (int) Math.floor((double) waitMinutes /60);
            int minutes = (int) (waitMinutes%60);
            if(minutes == 0) {
                p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], ChatColor.RED + "You will be up for vote in " + hours + " hours", 10, 70, 20);
            } else {
                p.sendTitle(ChatColor.RED + deathMessages[(int) (Math.random() * deathMessages.length)], ChatColor.RED + "You will be up for vote in " + hours + " hours and " + minutes + " minutes", 10, 70, 20);
            }
        }

        for(Nerd nerd : plugin.getNerds()) {
            nerd.getVotes().remove(uuid.toString());
        }

        // TODO: Do some particle effect here or something
    }

    public void revive() {

        // Revived players will no longer be dead, and should not be votable anymore
        dead = false;
        deadMinutes = 0;
        votable = false;
        votes = new ArrayList<>();

        // If the player is offline, just set them to be revived once they are back online
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            revived = true;
            return;
        }

        // Teleport the player to a random block around the map
        Location loc = config.getLocation("gameLocation").clone();
        double borderSize = config.getDouble("borderSize");
        double x = (loc.getX() + (Math.random() * borderSize) - (borderSize /2) - 4);
        double z = (loc.getZ() + (Math.random() * borderSize) - (borderSize /2) - 4);
        loc.setYaw((float) (Math.random()*360));
        p.teleport(loc.getWorld().getHighestBlockAt((int) x, (int) z).getLocation().add(0, 2, 0));

        // Let them know what happened
        p.sendTitle(ChatColor.GREEN + "You have been revived!", "", 10, 70, 20);

        // Allow them to interact with the world again
        p.setGameMode(GameMode.SURVIVAL);
        p.setInvisible(false);
        p.setAllowFlight(false);
        p.setHealth(20.0d);
        p.setFoodLevel(20);
        p.setCollidable(true);
        p.setInvulnerable(false);
        for (Player plr : Bukkit.getOnlinePlayers()) { plr.showPlayer(plugin, p); }

        // Remove all player potion effects (in case they have the darkness of the border)
        for(PotionEffect pe : p.getActivePotionEffects()) { p.removePotionEffect(pe.getType()); }
    }

    public boolean addVote(UUID uuid) {
        if(isVotable() && !votes.contains(uuid.toString())) {
            votes.add(uuid.toString());
            return true;
        }
        return false;
    }

    public boolean removeVote(UUID uuid) {
        if(isVotable() && votes.contains(uuid.toString())) {
            votes.add(uuid.toString());
            return true;
        }
        return false;
    }

    // How we load player data from config stuff
    public static Nerd deserialize(ConfigurationSection serializedData) {
        if (serializedData == null) {
            System.out.println("Serialized data is null! Cannot load Nerd");
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
