package me.pm7.claustrophobia;

import me.pm7.claustrophobia.Commands.*;
import me.pm7.claustrophobia.Listeners.*;
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
    public static DataManager dm;
    private static FileConfiguration data;
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        plugin = this;
        dm = new DataManager(this);
        data = dm.getConfig();
        config = getConfig();

        // Check for plugin updates
        new UpdateCheck(this, 120980).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().warning("There is a new Claustrophobia update available!");
                getLogger().warning("");
                getLogger().warning("The latest version is " + version);
                getLogger().warning("It is recommended that you look at the changelog for the latest version, as it may have some important changes/bug fixes");
            }
        });

        config.options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();

        // Load config data
        nerds = new ArrayList<>();
        ConfigurationSection nerdSection = data.getConfigurationSection("players");
        if(nerdSection == null) {
            data.createSection("players");
            dm.saveConfig();
        } else {
            for (String newNerd : nerdSection.getKeys(false)) {
                ConfigurationSection section = nerdSection.getConfigurationSection(newNerd);
                if (section == null) { return;}

                Nerd nerd = Nerd.deserialize(section);
                nerds.add(nerd);
            }
        }

        // If some owner is using /reload, this will make sure (*) that nothing breaks=
        for(Player p : Bukkit.getOnlinePlayers()) {
            Connections.checkPlayerDataOnLoad(p);
        }

        // Listeners and commands do be registered
        getServer().getPluginManager().registerEvents(new SpectatorManager(), plugin);
        getServer().getPluginManager().registerEvents(new NetherPortal(), plugin);
        getServer().getPluginManager().registerEvents(new Connections(), plugin);
        getServer().getPluginManager().registerEvents(new Border(), plugin);
        getServer().getPluginManager().registerEvents(new Death(), plugin);
        getServer().getPluginManager().registerEvents(new vote(), plugin);
        getCommand("votemenu").setExecutor(new vote());
        getCommand("endgame").setExecutor(new endgame());
        getCommand("startgame").setExecutor(new startgame());
        getCommand("reviveplayer").setExecutor(new reviveplayer());
        getCommand("claustrophobiainfo").setExecutor(new claustrophobiainfo());

        // Wall managing (removeWalls also sets up the new ones because reasons)
        removeWalls();

        // Start the game, border damage, and autosave loops
        new BukkitRunnable() { @Override public void run() {deadLoop();}}.runTaskTimer(plugin, 20L, 5L);
        new BukkitRunnable() { @Override public void run() {borderLoop();}}.runTaskTimer(plugin, 20L, 5L);
        new BukkitRunnable() { @Override public void run() {savePlayerData();}}.runTaskTimer(plugin, 20L, 2400L);
    }

    // Deals damage to players outside the border
    public void borderLoop() {
        Location loc = data.getLocation("gameLocation");
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
        }
    }

    // Runs dead timer and displays information to dead players
    int deadLoopTick = 1;
    public void deadLoop() {
        if(dm.getConfig().getBoolean("endgame")) {return;}
        if(deadLoopTick >= 240) { deadLoopTick = 0; }

        Location loc = data.getLocation("gameLocation");
        if(loc == null) {return;}

        // Display the death time left / amount of votes a dead player has, and tick up their minutes every minute
        for (Nerd nerd : nerds) {
            if(nerd.isDead()) {
                if (nerd.isVotable()) {

                    // Send an actionbar telling them how many votes they have
                    Player p = Bukkit.getPlayer(nerd.getUuid());
                    if (p == null) { continue; }
                    int neededVotes = 0;
                    for(Nerd n : nerds) { if(!n.isDead()) {neededVotes++;} }
                    neededVotes = (int) (neededVotes *  ((float) config.getInt("reviveVotePercentage")/100));
                    if(neededVotes == 0) {neededVotes++;}
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "You have " + nerd.getVotes().size() + "/" + neededVotes + " votes"));

                } else {
                    if (deadLoopTick == 1) { nerd.addMinute(); } // add a minute each minute

                    // Send an actionbar telling them what their current time is
                    Player p = Bukkit.getPlayer(nerd.getUuid());
                    if (p == null) { continue; }
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
        deadLoopTick++;
    }

    // Method for saving player data
    public void savePlayerData() {
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
    public Nerd getNerd(String name) {
        for(Nerd n : nerds) {
            if(n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    private static List<Nerd> nerds;
    public List<Nerd> getNerds() {return nerds;}

    public static Claustrophobia getPlugin() { return plugin; }
    public static DataManager getData() { return dm; }


    BlockDisplay northWall, eastWall, southWall, westWall; // Z-, X+. Z+, X-
    public void setupWalls() {
        Location loc = data.getLocation("gameLocation");
        if(loc == null) {return;}
        World w = loc.getWorld();
        double borderSize = config.getDouble("borderSize");

        Location nLoc = loc.clone().subtract(0, 0, borderSize/2 - 0.03);
        nLoc.setY(-250);
        northWall = (BlockDisplay) w.spawnEntity(nLoc, EntityType.BLOCK_DISPLAY);
        northWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        northWall.setTransformation(new Transformation(new Vector3f((float) -(borderSize-0.061125)/2, 0, 0), new AxisAngle4f(), new Vector3f((float) (borderSize-0.061125), 1000000, 0.001f), new AxisAngle4f()));
        northWall.setShadowRadius(0f);
        northWall.setViewRange(1000000000);
        northWall.setBrightness(new Display.Brightness(15, 15));
        data.set("northWall", northWall.getUniqueId().toString());

        Location eLoc = loc.clone().add(borderSize/2 - 0.03, 0, 0);
        eLoc.setY(-250);
        eastWall = (BlockDisplay) w.spawnEntity(eLoc, EntityType.BLOCK_DISPLAY);
        eastWall.setRotation(90.0f, 0f);
        eastWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        eastWall.setTransformation(new Transformation(new Vector3f((float) -(borderSize-0.061125)/2, 0, 0), new AxisAngle4f(), new Vector3f((float) (borderSize-0.061125), 1000000, 0.001f), new AxisAngle4f()));
        eastWall.setShadowRadius(0f);
        eastWall.setViewRange(1000000000);
        eastWall.setBrightness(new Display.Brightness(15, 15));
        data.set("eastWall", eastWall.getUniqueId().toString());

        Location sLoc = loc.clone().add(0, 0, borderSize/2 - 0.03);
        sLoc.setY(-250);
        southWall = (BlockDisplay) w.spawnEntity(sLoc, EntityType.BLOCK_DISPLAY);
        southWall.setRotation(180.0f, 0f);
        southWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        southWall.setTransformation(new Transformation(new Vector3f((float) -(borderSize-0.061125)/2, 0, 0), new AxisAngle4f(), new Vector3f((float) (borderSize-0.061125), 1000000, 0.001f), new AxisAngle4f()));
        southWall.setShadowRadius(0f);
        southWall.setViewRange(1000000000);
        southWall.setBrightness(new Display.Brightness(15, 15));
        data.set("southWall", southWall.getUniqueId().toString());

        Location wLoc = loc.clone().subtract(borderSize/2 - 0.03, 0, 0);
        wLoc.setY(-250);
        westWall = (BlockDisplay) w.spawnEntity(wLoc, EntityType.BLOCK_DISPLAY);
        westWall.setRotation(270.0f, 0f);
        westWall.setBlock(Material.NETHER_PORTAL.createBlockData());
        westWall.setTransformation(new Transformation(new Vector3f((float) -(borderSize-0.061125)/2, 0, 0), new AxisAngle4f(), new Vector3f((float) (borderSize-0.061125), 1000000, 0.001f), new AxisAngle4f()));
        westWall.setShadowRadius(0f);
        westWall.setViewRange(1000000000);
        westWall.setBrightness(new Display.Brightness(15, 15));
        data.set("westWall", westWall.getUniqueId().toString());

        dm.saveConfig();
    }

    int task = 0;
    public void removeWalls() {

        Location loc = data.getLocation("gameLocation");
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

                    if(data.getString("northWall") != null) {
                        if(uuid.equals(data.getString("northWall"))) { e1.remove();continue;  }
                    }
                    if(data.getString("eastWall") != null) {
                        if (uuid.equals(data.getString("eastWall"))) { e1.remove(); continue;  }
                    }
                    if(data.getString("southWall") != null) {
                        if (uuid.equals(data.getString("southWall"))) { e1.remove(); continue;  }
                    }
                    if(data.getString("westWall") != null) {
                        if (uuid.equals(data.getString("westWall"))) { e1.remove(); }
                    }
                }

                // Once we're done removing the previous session's walls, it's time to make the new ones and cancel this loop
                setupWalls();
                Bukkit.getScheduler().cancelTask(task);
            }
        }, 0L, 20L); // Run every two ticks. I like the number two
    }
}
