package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.model.type.ItemsType;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public final class CrystalOfSpaceListener implements Listener {

  private final ItemsPlugin plugin;

  @EventHandler
  public void onChat(final PlayerChatEvent event) {
    final Player player = event.getPlayer();

    final ItemStack crystalOfSpace = plugin.getFactory().getItems().get(ItemsType.CRYSTAL_OF_SPACE);
    if (crystalOfSpace == null) {
      return;
    }

    final ItemStack itemInHand = player.getInventory().getItemInMainHand();
    if (!itemInHand.isSimilar(crystalOfSpace)) {
      return;
    }

    final String[] values = event.getMessage().split(";");
    final List<Integer> coordinates = new LinkedList<>();

    for (String value : values) {
      try {
        final Integer integer = Integer.parseInt(value);
        coordinates.add(integer);
      } catch (Exception ignored) {}
    }

    final Location location = new Location(player.getWorld(), coordinates.get(0), coordinates.get(1), coordinates.get(2));
    player.teleport(location);

    player.sendMessage("§aYou have been teleported to the location typed!");
    event.setCancelled(true);
  }

}
