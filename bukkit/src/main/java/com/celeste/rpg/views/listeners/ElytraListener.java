package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.model.type.ItemsType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public final class ElytraListener implements Listener {

  private final ItemStack wings;

  public ElytraListener(final ItemsPlugin plugin) {
    this.wings = plugin.getFactory().getItems().get(ItemsType.WINGS_OF_ETERNAL_FLIGHT);
  }

  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();

    final ItemStack chestplate = player.getInventory().getChestplate();
    if (chestplate == null || !hasSpecialElytra(chestplate)) {
      return;
    }

    final Material material = player.getLocation().getBlock().getType();
    if (player.isGliding() && material == Material.AIR) {
      player.setGliding(true);
      player.setSprinting(true);

      player.setVelocity(player.getLocation().getDirection().multiply(2D));
    }
  }

  private boolean hasSpecialElytra(final ItemStack item) {
    return Bukkit.getItemFactory().equals(wings.getItemMeta(), item.getItemMeta());
  }

}
