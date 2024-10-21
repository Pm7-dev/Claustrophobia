package me.pm7.minihardcore.Listeners;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuListener implements Listener {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    boolean dontSave = false;

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equals(ChatColor.RED + "Vote Menu")) {
            dontSave = false;
            HashSet<String> dead = plugin.getDead();
            ArrayList<String> VoteList1 = plugin.getVoteList1();
            ArrayList<String> VoteList2 = plugin.getVoteList2();

            if(e.getCurrentItem() != null) {
                ItemStack item = e.getCurrentItem();
                ItemMeta itemMeta = item.getItemMeta();
                String deadName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();

                if(item.getType() == Material.LIME_STAINED_GLASS_PANE && Objects.equals(itemMeta.getLore(), Collections.singletonList("Click here"))) {
                    if(dead.contains(deadName)) {
                        VoteList1.add(p.getName());
                        VoteList2.add(deadName);
                        if(Bukkit.getPlayer(deadName) != null) {
                            Player deadPlayer = Bukkit.getPlayer(deadName);
                            plugin.setVoteList1(VoteList1);
                            plugin.setVoteList2(VoteList2);
                            plugin.checkRevive(deadPlayer);
                            dontSave = true;
                        }
                        item.setType(Material.RED_STAINED_GLASS_PANE);
                    }
                } else if (item.getType() == Material.RED_STAINED_GLASS_PANE && Objects.equals(itemMeta.getLore(), Collections.singletonList("Click here"))) {
                    if(dead.contains(deadName)) {
                        int size = VoteList1.size();
                        for (int i = size; i > 0; i--) {
                            String entry = VoteList1.get(i-1);
                            if (entry.equals(p.getName())) {
                                if (Objects.equals(VoteList2.get(i-1), deadName)) {
                                    VoteList1.remove(i-1);
                                    VoteList2.remove(i-1);
                                    item.setType(Material.LIME_STAINED_GLASS_PANE);
                                }
                            }
                        }
                    }
                }
            }
            if(!dontSave) {
                plugin.setVoteList1(VoteList1);
                plugin.setVoteList2(VoteList2);
            }

            e.setCancelled(true);
        }
        if(e.getView().getTitle().equals(ChatColor.RED + "Shop Menu")) {
            HashMap<String, Integer> Janbonium = plugin.getJanbonium();

            if(e.getCurrentItem() != null) {
                ItemStack item = e.getCurrentItem();
                if(!Janbonium.containsKey(p.getName())) {
                    Janbonium.put(p.getName(), 0);
                }
                int pJanbonium = Janbonium.get(p.getName());
                if(item.getType() == Material.OAK_SAPLING && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 100"))) {
                    if(pJanbonium >= 100) {
                        int newJanbonium = pJanbonium - 100;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.OAK_SAPLING, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.SUGAR_CANE && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 200"))) {
                    if(pJanbonium >= 200) {
                        int newJanbonium = pJanbonium - 200;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.SUGAR_CANE, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.LEATHER && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 350"))) {
                    if(pJanbonium >= 350) {
                        int newJanbonium = pJanbonium - 350;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.LEATHER, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.COAL && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 50"))) {
                    if(pJanbonium >= 50) {
                        int newJanbonium = pJanbonium - 50;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.COAL, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.IRON_INGOT && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 250"))) {
                    if(pJanbonium >= 250) {
                        int newJanbonium = pJanbonium - 250;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.WATER_BUCKET && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 750"))) {
                    if(pJanbonium >= 750) {
                        int newJanbonium = pJanbonium - 750;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.POINTED_DRIPSTONE && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 500"))) {
                    if(pJanbonium >= 500) {
                        int newJanbonium = pJanbonium - 500;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.POINTED_DRIPSTONE, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.GOLDEN_APPLE && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 2000"))) {
                    if(pJanbonium >= 2000) {
                        int newJanbonium = pJanbonium - 2000;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.CHORUS_FRUIT && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 500"))) {
                    if(pJanbonium >= 500) {
                        int newJanbonium = pJanbonium - 500;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.CHORUS_FRUIT, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.DIAMOND && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 2400"))) {
                    if(pJanbonium >= 2400) {
                        int newJanbonium = pJanbonium - 2400;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.SHULKER_BOX && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 7000"))) {
                    if(pJanbonium >= 7000) {
                        int newJanbonium = pJanbonium - 7000;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.SHULKER_BOX, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.END_CRYSTAL && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 15000"))) {
                    if(pJanbonium >= 15000) {
                        int newJanbonium = pJanbonium - 15000;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.END_CRYSTAL, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.ENDER_PEARL && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 3000"))) {
                    if(pJanbonium >= 3000) {
                        int newJanbonium = pJanbonium - 3000;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.TNT && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 1500"))) {
                    if(pJanbonium >= 1500) {
                        int newJanbonium = pJanbonium - 1500;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.TNT, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.SCULK_SENSOR && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 7500"))) {
                    if(pJanbonium >= 7500) {
                        int newJanbonium = pJanbonium - 7500;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.SCULK_SENSOR, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.AXOLOTL_BUCKET && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 7500"))) {
                    if(pJanbonium >= 7500) {
                        int newJanbonium = pJanbonium - 7500;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.AXOLOTL_BUCKET, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.TRIDENT && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 7500"))) {
                    if(pJanbonium >= 7500) {
                        int newJanbonium = pJanbonium - 7500;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.TRIDENT, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }

                if(item.getType() == Material.BEETROOT && Objects.equals(item.getItemMeta().getLore(), Collections.singletonList("Price: 100000"))) {
                    if(pJanbonium >= 100000) {
                        int newJanbonium = pJanbonium - 100000;
                        Janbonium.replace(p.getName(), newJanbonium);
                        p.getInventory().addItem(new ItemStack(Material.BEETROOT, 1));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 30);
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough Janbonium for this item");
                    }
                }
                plugin.loadScoreBoard(p);
            }
            plugin.setJanbonium(Janbonium);
            e.setCancelled(true);
        }
    }
}
