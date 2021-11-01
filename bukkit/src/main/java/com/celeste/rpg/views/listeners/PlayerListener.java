package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.model.PlayerItemCooldown;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@AllArgsConstructor
public final class PlayerListener implements Listener {

  private final ItemsPlugin plugin;

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final UUID id = event.getPlayer().getUniqueId();
    plugin.getFactory().getPlayerCooldown().register(id, new PlayerItemCooldown(id));
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    plugin.getController().removeFromRegistry(event.getPlayer());
  }

  @EventHandler
  public void onPlayerKick(final PlayerKickEvent event) {
    plugin.getController().removeFromRegistry(event.getPlayer());
  }

}
