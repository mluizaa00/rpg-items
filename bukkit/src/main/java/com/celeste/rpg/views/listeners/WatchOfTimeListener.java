package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@AllArgsConstructor
public final class WatchOfTimeListener implements Listener {

  private final ItemsPlugin plugin;

  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    if (!plugin.getController().isInWatchOfTime() || event.getPlayer().getUniqueId() == plugin.getController().getUsedWatchOfTime()) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler
  public void onDamage(final EntityDamageByEntityEvent event) {
    if (!plugin.getController().isInWatchOfTime()) {
      return;
    }

    if (event.getDamager().getUniqueId() == plugin.getController().getUsedWatchOfTime()) {
      event.setCancelled(false);
      return;
    }

    event.setCancelled(true);
  }

}
