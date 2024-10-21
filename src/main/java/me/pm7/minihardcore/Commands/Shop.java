package me.pm7.minihardcore.Commands;

import me.pm7.minihardcore.MiniHardcore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;

import static me.pm7.minihardcore.MiniHardcore.started;

public class Shop implements CommandExecutor {
    MiniHardcore plugin = MiniHardcore.getPlugin();
    FileConfiguration config = plugin.getConfig();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(started) {
            if(sender instanceof Player) {
                HashSet<String> dead = plugin.getDead();
                if(!dead.contains(sender.getName())) {
                    Player p = (Player) sender;
                    Inventory inventory = Bukkit.createInventory(p, 18, ChatColor.RED + "Shop Menu");

                    ItemStack oakSapling = new ItemStack(Material.OAK_SAPLING, 1);
                    ItemMeta oakSaplingItemMeta = oakSapling.getItemMeta();
                    oakSaplingItemMeta.setLore(Arrays.asList("Price: 100"));
                    oakSapling.setItemMeta(oakSaplingItemMeta);
                    inventory.setItem(0, oakSapling);

                    ItemStack sugarCane = new ItemStack(Material.SUGAR_CANE, 1);
                    ItemMeta sugarCaneItemMeta = sugarCane.getItemMeta();
                    sugarCaneItemMeta.setLore(Arrays.asList("Price: 200"));
                    sugarCane.setItemMeta(sugarCaneItemMeta);
                    inventory.setItem(1, sugarCane);

                    ItemStack leather = new ItemStack(Material.LEATHER, 1);
                    ItemMeta leatherItemMeta = leather.getItemMeta();
                    leatherItemMeta.setLore(Arrays.asList("Price: 350"));
                    leather.setItemMeta(leatherItemMeta);
                    inventory.setItem(2, leather);

                    ItemStack coal = new ItemStack(Material.COAL, 1);
                    ItemMeta coalItemMeta = coal.getItemMeta();
                    coalItemMeta.setLore(Arrays.asList("Price: 50"));
                    coal.setItemMeta(coalItemMeta);
                    inventory.setItem(3, coal);

                    ItemStack iron = new ItemStack(Material.IRON_INGOT, 1);
                    ItemMeta ironItemMeta = iron.getItemMeta();
                    ironItemMeta.setLore(Arrays.asList("Price: 250"));
                    iron.setItemMeta(ironItemMeta);
                    inventory.setItem(4, iron);

                    ItemStack water = new ItemStack(Material.WATER_BUCKET, 1);
                    ItemMeta waterItemMeta = water.getItemMeta();
                    waterItemMeta.setLore(Arrays.asList("Price: 750"));
                    water.setItemMeta(waterItemMeta);
                    inventory.setItem(5, water);

                    ItemStack dripstone = new ItemStack(Material.POINTED_DRIPSTONE, 1);
                    ItemMeta dripstoneItemMeta = dripstone.getItemMeta();
                    dripstoneItemMeta.setLore(Arrays.asList("Price: 500"));
                    dripstone.setItemMeta(dripstoneItemMeta);
                    inventory.setItem(6, dripstone);

                    ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1);
                    ItemMeta gappleItemMeta = gapple.getItemMeta();
                    gappleItemMeta.setLore(Arrays.asList("Price: 2000"));
                    gapple.setItemMeta(gappleItemMeta);
                    inventory.setItem(7, gapple);

                    ItemStack chorusFruit = new ItemStack(Material.CHORUS_FRUIT, 1);
                    ItemMeta chorusFruitItemMeta = chorusFruit.getItemMeta();
                    chorusFruitItemMeta.setLore(Arrays.asList("Price: 500"));
                    chorusFruit.setItemMeta(chorusFruitItemMeta);
                    inventory.setItem(8, chorusFruit);

                    if (config.getBoolean("EndGame")) {
                        ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
                        ItemMeta diamondItemMeta = oakSapling.getItemMeta();
                        diamondItemMeta.setLore(Arrays.asList("Price: 2400"));
                        diamond.setItemMeta(diamondItemMeta);
                        inventory.setItem(9, diamond);

                        ItemStack shulkerBox = new ItemStack(Material.SHULKER_BOX, 1);
                        ItemMeta shulkerBoxItemMeta = oakSapling.getItemMeta();
                        shulkerBoxItemMeta.setLore(Arrays.asList("Price: 7000"));
                        shulkerBox.setItemMeta(shulkerBoxItemMeta);
                        inventory.setItem(10, shulkerBox);

                        ItemStack endCrystal = new ItemStack(Material.END_CRYSTAL, 1);
                        ItemMeta endCrystalItemMeta = oakSapling.getItemMeta();
                        endCrystalItemMeta.setLore(Arrays.asList("Price: 15000"));
                        endCrystal.setItemMeta(endCrystalItemMeta);
                        inventory.setItem(11, endCrystal);

                        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL, 1);
                        ItemMeta enderPearlItemMeta = oakSapling.getItemMeta();
                        enderPearlItemMeta.setLore(Arrays.asList("Price: 3000"));
                        enderPearl.setItemMeta(enderPearlItemMeta);
                        inventory.setItem(12, enderPearl);

                        ItemStack TNT = new ItemStack(Material.TNT, 1);
                        ItemMeta TNTItemMeta = oakSapling.getItemMeta();
                        TNTItemMeta.setLore(Arrays.asList("Price: 1500"));
                        TNT.setItemMeta(TNTItemMeta);
                        inventory.setItem(13, TNT);

                        ItemStack sculkSensor = new ItemStack(Material.SCULK_SENSOR, 1);
                        ItemMeta sculkSensorItemMeta = oakSapling.getItemMeta();
                        sculkSensorItemMeta.setLore(Arrays.asList("Price: 7500"));
                        sculkSensor.setItemMeta(sculkSensorItemMeta);
                        inventory.setItem(14, sculkSensor);

                        ItemStack axolotl = new ItemStack(Material.AXOLOTL_BUCKET, 1);
                        ItemMeta axolotlItemMeta = oakSapling.getItemMeta();
                        axolotlItemMeta.setLore(Arrays.asList("Price: 7500"));
                        axolotl.setItemMeta(axolotlItemMeta);
                        inventory.setItem(15, axolotl);

                        ItemStack trident = new ItemStack(Material.TRIDENT, 1);
                        ItemMeta tridentItemMeta = oakSapling.getItemMeta();
                        tridentItemMeta.setLore(Arrays.asList("Price: 7500"));
                        trident.setItemMeta(tridentItemMeta);
                        inventory.setItem(16, trident);

                        ItemStack beetroot = new ItemStack(Material.BEETROOT, 1);
                        ItemMeta beetrootItemMeta = oakSapling.getItemMeta();
                        beetrootItemMeta.setLore(Arrays.asList("Price: 100000"));
                        beetroot.setItemMeta(beetrootItemMeta);
                        inventory.setItem(17, beetroot);
                    }

                    p.openInventory(inventory);
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "The game has not started yet");
        }
        return true;
    }
}
