package com.celeste.rpg.views.menu;

import com.celeste.library.spigot.util.menu.BorderShape;
import com.celeste.rpg.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class PlayerInventory {

  public static Inventory getInventory() {
    final Inventory inventory = Bukkit.createInventory(null, 9 * 5, "Select player to create portal");
    final int[] shape = BorderShape.FIVE.getShape();

    int count = 0;
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      inventory.setItem(shape[count], getPlayer(onlinePlayer));
    }

    return inventory;
  }

  private static ItemStack getPlayer(final Player player) {
    return new ItemBuilder(Material.PLAYER_HEAD)
        .name("§7" + player.getName())
        .skull(player.getName())
        .lore("§aClick to here to select", "", "§8" + player.getUniqueId())
        .build();
  }

}
