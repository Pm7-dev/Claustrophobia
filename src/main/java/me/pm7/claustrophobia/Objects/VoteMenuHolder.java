package me.pm7.claustrophobia.Objects;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

// Custom  way to identify what my special little vote inventory is

public class VoteMenuHolder implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(null, 9, "hiiiii :3");
    }
}
