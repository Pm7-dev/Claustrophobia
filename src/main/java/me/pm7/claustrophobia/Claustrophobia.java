package me.pm7.claustrophobia;

import me.pm7.claustrophobia.Commands.reviveplayer;
import me.pm7.claustrophobia.Commands.startgame;
import me.pm7.claustrophobia.Commands.vote;
import me.pm7.claustrophobia.Listeners.Connections;
import me.pm7.claustrophobia.Listeners.Death;
import me.pm7.claustrophobia.Listeners.DenySpectatorInteraction;
import me.pm7.claustrophobia.Listeners.Movement;
import me.pm7.claustrophobia.Objects.Nerd;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public final class Claustrophobia extends JavaPlugin {

    private static Claustrophobia plugin;
    public DataManager dm;
    private static FileConfiguration data;
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        plugin = this;
        dm = new DataManager(this);
        data = dm.getConfig();
        config = getConfig();

        config.options().copyDefaults();
        saveDefaultConfig();

        // Load config data
        nerds = new ArrayList<>();
        ConfigurationSection nerdSection = data.getConfigurationSection("players");
        if(nerdSection == null) {
            data.createSection("players");
            dm.saveConfig();
            return;
        }
        for (String newNerd : nerdSection.getKeys(false)) {
            ConfigurationSection section = nerdSection.getConfigurationSection(newNerd);
            if(section == null) { System.out.println("idk how this happened"); return; }

            Nerd nerd = Nerd.deserialize(section);
            nerds.add(nerd);
        }

        // Listeners do be registered
        getServer().getPluginManager().registerEvents(new DenySpectatorInteraction(), plugin);
        getServer().getPluginManager().registerEvents(new Connections(), plugin);
        getServer().getPluginManager().registerEvents(new Movement(), plugin);
        getServer().getPluginManager().registerEvents(new Death(), plugin);
        getServer().getPluginManager().registerEvents(new vote(), plugin);
        getCommand("reviveplayer").setExecutor(new reviveplayer()); // (commands also be registered)
        getCommand("startgame").setExecutor(new startgame());
        getCommand("votemenu").setExecutor(new vote());

        // Wall managing (removeWalls also sets up the new ones because reasons)
        removeWalls();

        // Start the game loop and autosave
        new BukkitRunnable() { @Override public void run() {gameLoop();}}.runTaskTimer(plugin, 20L, 5L);
        new BukkitRunnable() { @Override public void run() {autosave();}}.runTaskTimer(plugin, 20L, 2400L);
    }

    @Override
    public void onDisable() {
        // Save player data
        ConfigurationSection nerdsSection = data.createSection("players");
        for (Nerd nerd : nerds) {
            ConfigurationSection nerdSection = nerdsSection.createSection(nerd.getName());
            for (Map.Entry<String, Object> entry : nerd.serialize().entrySet()) {
                nerdSection.set(entry.getKey(), entry.getValue());
            }
        }
        dm.saveConfig();
    }

    int tick = 1; // how scandalous
    public void gameLoop() {
        if(tick >= 240) { tick = 0; }

        Location loc = config.getLocation("gameLocation");
        if(loc == null) {return;}
        double borderSize = config.getDouble("borderSize");

        for (Nerd nerd : nerds) {

            Player p = Bukkit.getPlayer(nerd.getUuid());
            if (p == null) { continue; }

            // Deal border damage
            if(!nerd.isDead()) {
                if (nerd.isOutOfBorder()) {
                    Location pLoc = p.getLocation();
                    double diffX = Math.abs(loc.getX() - pLoc.getX()) - (borderSize / 2);
                    double diffZ = Math.abs(loc.getZ() - pLoc.getZ()) - (borderSize / 2);
                    double greatestDistance = Math.max(diffX, diffZ);
                    p.setNoDamageTicks(0);
                    p.damage(greatestDistance - 0.75f);

                    boolean needsDarkness = true;
                    for (PotionEffect pe : p.getActivePotionEffects()) {
                        if (pe.getType() == PotionEffectType.DARKNESS) {
                            needsDarkness = false;
                            break;
                        }
                    }
                    if (needsDarkness) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, PotionEffect.INFINITE_DURATION, 0, true));
                    }
                } else {
                    PotionEffect pe = p.getPotionEffect(PotionEffectType.DARKNESS);
                    if (pe != null && pe.getDuration() == PotionEffect.INFINITE_DURATION) {
                        p.removePotionEffect(PotionEffectType.DARKNESS);
                    }
                }
            }

            // Display the death time left / amount of votes a dead player has
            if(nerd.isDead()) {
                if (nerd.isVotable()) {
                    int neededVotes = 0;
                    for(Nerd n : nerds) { if(!n.isDead()) {neededVotes++;} }
                    neededVotes = (int) (neededVotes *  ((float) config.getInt("reviveVotePercentage")/100));
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "You have " + nerd.getVotes().size() + "/" + neededVotes + " votes"));
                } else {
                    if (tick == 1) { nerd.addMinute(); } // add a minute each minute
                    if(nerd.getMinutes() != 0) { // They've just seen the title thingy, no need to immediately broadcast this
                        long waitMinutes = config.getLong("deathTime");
                        int hours = (int) ((waitMinutes - nerd.getMinutes()) / 60);
                        int minutes = (int) ((waitMinutes - nerd.getMinutes()) % 60);
                        if (minutes == 0) {
                            String hrsTxt;
                            if(hours == 1) {hrsTxt = hours + " hour";} else {hrsTxt = hours + " hours";}
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "You will be up for vote in " + hrsTxt));
                        } else {
                            String hrsTxt;
                            if(hours == 1) {hrsTxt = hours + " hour";} else {hrsTxt = hours + " hours";}
                            String minsTxt;
                            if(minutes == 1) {minsTxt = minutes + " minute";} else {minsTxt = minutes + " minutes";}
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "You will be up for vote in " + hrsTxt + " and " + minsTxt));
                        }
                    }
                }
            }
        }
        tick++;
    }

    // Method for saving player data
    public void autosave() {
        ConfigurationSection nerdsSection = data.createSection("players");
        for (Nerd nerd : nerds) {
            ConfigurationSection nerdSection = nerdsSection.createSection(nerd.getName());
            for (Map.Entry<String, Object> entry : nerd.serialize().entrySet()) {
                nerdSection.set(entry.getKey(), entry.getValue());
            }
        }
        dm.saveConfig();
    }

    public Nerd getNerd(UUID uuid) {
        for(Nerd n : nerds) {
            if(n.getUuid().toString().equals(uuid.toString())) {
                return n;
            }
        }
        return null;
    }

    private static List<Nerd> nerds;
    public List<Nerd> getNerds() {return nerds;}

    public static Claustrophobia getPlugin() { return plugin; }


    BlockDisplay northWall, eastWall, southWall, westWall; // Z-, X+. Z+, X-
    public void setupWalls() {
        Location loc = config.getLocation("gameLocation");
        if(loc == null) {return;}
        World w = loc.getWorld();
        double borderSize = config.getDouble("borderSize");

        Location nLoc = loc.clone().subtract(0, 500, borderSize/2);
        northWall = (BlockDisplay) w.spawnEntity(nLoc, EntityType.BLOCK_DISPLAY);
        northWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        northWall.setTransformation(new Transformation(new Vector3f((float) (-borderSize/2), 0, 0), new AxisAngle4f(), new Vector3f((float) borderSize, 1000000, 0.01f), new AxisAngle4f()));
        northWall.setShadowRadius(0f);
        northWall.setViewRange(1000000000);
        northWall.setBrightness(new Display.Brightness(15, 15));
        config.set("northWall", northWall.getUniqueId().toString());

        Location eLoc = loc.clone().add(borderSize/2, -500, 0);
        eastWall = (BlockDisplay) w.spawnEntity(eLoc, EntityType.BLOCK_DISPLAY);
        eastWall.setRotation(90.0f, 0f);
        eastWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        eastWall.setTransformation(new Transformation(new Vector3f((float) (-borderSize/2), 0, 0), new AxisAngle4f(), new Vector3f((float) borderSize, 1000000, 0.01f), new AxisAngle4f()));
        eastWall.setShadowRadius(0f);
        eastWall.setViewRange(1000000000);
        eastWall.setBrightness(new Display.Brightness(15, 15));
        config.set("eastWall", eastWall.getUniqueId().toString());

        Location sLoc = loc.clone().add(0, -500, borderSize/2);
        southWall = (BlockDisplay) w.spawnEntity(sLoc, EntityType.BLOCK_DISPLAY);
        southWall.setRotation(180.0f, 0f);
        southWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        southWall.setTransformation(new Transformation(new Vector3f((float) (-borderSize/2), 0, 0), new AxisAngle4f(), new Vector3f((float) borderSize, 1000000, 0.01f), new AxisAngle4f()));
        southWall.setShadowRadius(0f);
        southWall.setViewRange(1000000000);
        southWall.setBrightness(new Display.Brightness(15, 15));
        config.set("southWall", southWall.getUniqueId().toString());

        Location wLoc = loc.clone().subtract(borderSize/2, 500, 0);
        westWall = (BlockDisplay) w.spawnEntity(wLoc, EntityType.BLOCK_DISPLAY);
        westWall.setRotation(270.0f, 0f);
        westWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        westWall.setTransformation(new Transformation(new Vector3f((float) (-borderSize/2), 0, 0), new AxisAngle4f(), new Vector3f((float) borderSize, 1000000, 0.01f), new AxisAngle4f()));
        westWall.setShadowRadius(0f);
        westWall.setViewRange(1000000000);
        westWall.setBrightness(new Display.Brightness(15, 15));
        config.set("westWall", westWall.getUniqueId().toString());

        saveConfig();
    }

    int task = 0;
    private void removeWalls() {

        Location loc = config.getLocation("gameLocation");
        double borderSize = config.getDouble("borderSize");
        if(loc == null) {return;}
        World world = loc.getWorld();

        // Get the chunks that will contain the walls and load them
        Chunk n = world.getBlockAt((int) (loc.getX() - (borderSize/2)), 0, (int) loc.getZ()).getChunk();
        Chunk e = world.getBlockAt((int) (loc.getX() + (borderSize/2)), 0, (int) loc.getZ()).getChunk();
        Chunk s = world.getBlockAt((int) loc.getX(), 0, (int) (loc.getZ() - (borderSize/2))).getChunk();
        Chunk w = world.getBlockAt((int) loc.getX(), 0, (int) (loc.getZ() + (borderSize/2))).getChunk();
        n.load(); e.load(); s.load(); w.load();

        //Make a loop that checks to see if these chunks are loaded yet. Once they are, try to remove the entities based on the UUIDs in config
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(n.isEntitiesLoaded() && e.isEntitiesLoaded() && s.isEntitiesLoaded() && w.isEntitiesLoaded()) {

                // Remove the walls once all the chunks containing them are loaded
                for(Entity e1 : world.getEntities()) {
                    String uuid = e1.getUniqueId().toString();

                    if(config.getString("northWall") != null) {
                        if(uuid.equals(config.getString("northWall"))) { e1.remove();continue;  }
                    }
                    if(config.getString("eastWall") != null) {
                        if (uuid.equals(config.getString("eastWall"))) { e1.remove(); continue;  }
                    }
                    if(config.getString("southWall") != null) {
                        if (uuid.equals(config.getString("southWall"))) { e1.remove(); continue;  }
                    }
                    if(config.getString("westWall") != null) {
                        if (uuid.equals(config.getString("westWall"))) { e1.remove(); }
                    }
                }

                // Once we're done removing the previous session's walls, it's time to make the new ones and cancel this loop
                setupWalls();
                Bukkit.getScheduler().cancelTask(task);
            }
        }, 0L, 20L); // Run every two ticks. I like the number two
    }
}
