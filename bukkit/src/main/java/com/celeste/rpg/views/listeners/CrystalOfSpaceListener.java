package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@AllArgsConstructor
public final class CrystalOfSpaceListener implements Listener {

  private final ItemsPlugin plugin;

  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    if (!player.hasMetadata("portals")) {
      return;
    }

    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    for (Location key : plugin.getFactory().getPortals().getKeys()) {
      if (key.getWorld() == null || !key.getWorld().getName().equals(world.getName())) {
        continue;
      }

      final double distance = key.distance(location);
      if (distance > 2) {
        continue;
      }

      final Player target = plugin.getFactory().getPortals().get(key);
      if (target == null) {
        return;
      }

      player.teleport(target);
      player.sendMessage("§aYou have been teleported to " + target.getName() + "!");
    }
  }

}
