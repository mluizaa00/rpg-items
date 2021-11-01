package com.celeste.rpg.views.menu;

import com.celeste.library.spigot.util.menu.BorderShape;
import com.celeste.rpg.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerInventory {

  public static final Map<Integer, UUID> PLAYERS;

  static {
    PLAYERS = new HashMap<>();
  }

  public static Inventory getInventory() {
    final Inventory inventory = Bukkit.createInventory(null, 9 * 5, "Select player to create portal");
    PLAYERS.clear();

    final int[] shape = BorderShape.FIVE.getShape();

    int count = 0;
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      inventory.setItem(shape[count], getPlayer(onlinePlayer));
      PLAYERS.put(count++, onlinePlayer.getUniqueId());
    }

    return inventory;
  }

  private static ItemStack getPlayer(final Player player) {
    return new ItemBuilder(Material.PLAYER_HEAD)
        .skull(player.getName())
        .lore("§aClick to here to select")
        .build();
  }

}
