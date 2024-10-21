package me.pm7.minihardcore;

import me.pm7.minihardcore.Commands.*;
import me.pm7.minihardcore.Listeners.DenySpectatorInteraction;
import me.pm7.minihardcore.Listeners.MenuListener;
import me.pm7.minihardcore.Listeners.PlayerDeathListener;
import me.pm7.minihardcore.Listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.*;

public final class MiniHardcore extends JavaPlugin {

    private static MiniHardcore plugin;

    public static boolean started = false;
    @Override
    public void onEnable() {
        plugin = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        loadConfig();
        started = plugin.getConfig().getBoolean("Started");

        registerEvents();
        System.out.println("MiniHardcore plugin started");
    }

    @Override
    public void onDisable() {
        System.out.println("paint");
    }

    public void checkRevive(Player p) {
        System.out.println("checking revives for " + p.getName());
        ArrayList<String> VoteList2 = getVoteList2();
        ArrayList<String> VoteList1 = getVoteList1();
        HashSet<String> dead = getDead();
        int votes = 0;
        for (String entry : VoteList2) {
            if (entry.equals(p.getName())) {
                votes++;
            }
        }
        double players = getConfig().getInt("Players");
        double votesNeeded = Math.ceil(players / 2);
        if(votes >= votesNeeded) {
            System.out.println("vote passed!");
            System.out.println("current voteList2 size: " + VoteList2.size());
            int size = VoteList2.size();
            for(int i = size; i > 0; i--) {
                System.out.println("current index: " + (i-1));
                String entry = VoteList2.get(i-1);
                System.out.println("current entry: " + entry);
                System.out.println("p.getname: " + p.getName());
                if(entry.equals(p.getName())) {
                    System.out.println("entry matches, deleting");
                    VoteList1.remove(i-1);
                    VoteList2.remove(i-1);
                    System.out.println("entry deleted");
                }
            }
            p.setInvulnerable(false);
            p.setInvisible(false);
            p.setAllowFlight(false);
            p.setFoodLevel(20);
            p.setHealth(20);

            Block block = p.getWorld().getHighestBlockAt(p.getLocation());
            p.teleport(block.getLocation().add(0, 1, 1));

            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 5));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 255));

            dead.remove(p.getName());
            getConfig().set("Players", getConfig().getInt("Players") + 1);
            saveConfig();
            setVoteList2(VoteList2);
            setVoteList1(VoteList1);
            setDead(dead);
            System.out.println("saving lists");
        }
    }

    public void loadScoreBoard(Player p) {
        HashMap<String, Integer> Janbonium = getJanbonium();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("janbonium", "dummy", "Janbonium");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = objective.getScore(p.getName());
        if(Janbonium.get(p.getName()) != null) {
            score.setScore(Janbonium.get(p.getName()));
            p.setScoreboard(scoreboard);
        }
    }

    void loadConfig() {
        if (getConfig().getConfigurationSection("Janbonium") == null) {
            getConfig().createSection("Janbonium");
        }
        if (getConfig().getConfigurationSection("VoteList1") == null) {
            getConfig().createSection("VoteList1");
        }
        if (getConfig().getConfigurationSection("VoteList2") == null) {
            getConfig().createSection("VoteList2");
        }
        if (getConfig().getConfigurationSection("dead") == null) {
            getConfig().createSection("dead");
        }
    }

    public HashMap<String, Integer> getJanbonium() {
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        for (String key : getConfig().getConfigurationSection("Janbonium").getKeys(false)) {
            hm.put(key, getConfig().getInt("Janbonium."+key));
        }
        return hm;
    }
    public void setJanbonium(HashMap<String, Integer> hashMap) {
        for (String key : hashMap.keySet()) {
            getConfig().set("Janbonium."+key, hashMap.get(key));
        }
        saveConfig();
    }
    public ArrayList<String> getVoteList1() {
        List<String> bList = plugin.getConfig().getStringList("VoteList1");
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(bList);
        return list;
    }
    public void setVoteList1(ArrayList<String> list) {
        plugin.getConfig().set("VoteList1", list);
        saveConfig();
    }
    public ArrayList<String> getVoteList2() {
        List<String> bList = plugin.getConfig().getStringList("VoteList2");
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(bList);
        return list;
    }
    public void setVoteList2(ArrayList<String> list) {
        plugin.getConfig().set("VoteList2", list);
        saveConfig();
    }
    public HashSet<String> getDead() {
        HashSet<String> set = new HashSet<String>();
        set.addAll(getConfig().getStringList("dead"));
        return set;
    }
    public void setDead(HashSet<String> hashSet) {
        List<String> list = new ArrayList<String>();
        list.addAll(hashSet);
        getConfig().set("dead", list);
        saveConfig();
    }
    void registerEvents() {
        getCommand("vote").setExecutor(new Vote());
        getCommand("sell").setExecutor(new Sell());
        getCommand("start").setExecutor(new Start());
        getCommand("shop").setExecutor(new Shop());
        getCommand("test").setExecutor(new Test());
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new DenySpectatorInteraction(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);

    }
    public static MiniHardcore getPlugin() {
        return plugin;
    }
}
