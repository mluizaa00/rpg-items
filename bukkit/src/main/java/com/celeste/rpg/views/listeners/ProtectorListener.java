package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.model.type.ItemsType;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public final class ProtectorListener implements Listener {

  private final ItemsPlugin plugin;
  private final ItemStack chestplate;

  public ProtectorListener(final ItemsPlugin plugin) {
    this.plugin = plugin;
    this.chestplate = plugin.getFactory().getItems().get(ItemsType.ZOMBIE_LORDS_CHESTPLATE);
  }

  @EventHandler
  public void onTarget(final EntityTargetEvent event) {
    final Entity entity = event.getEntity();
    if (event.getTarget() == null || !entity.hasMetadata("owner")) {
      return;
    }

    if (event.getTarget().getName().equalsIgnoreCase(entity.getMetadata("owner").get(0).asString())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void equipChestplate(final PlayerInteractEvent event) {
    if (!event.hasItem() || !event.getAction().name().contains("RIGHT")) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null || !item.hasItemMeta() || !item.isSimilar(chestplate)) {
      return;
    }

    final Player player = event.getPlayer();
    if (player.getInventory().getChestplate() != null) {
      return;
    }

    plugin.getController().executeZombieLordsChestplate(player);
  }

  @EventHandler
  public void unequipChestplate(final InventoryClickEvent event) {
    if (!event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
      return;
    }

    final ItemStack item = event.getCurrentItem();
    if (item == null || !item.hasItemMeta() || !item.isSimilar(chestplate)) {
      return;
    }

    if (event.getAction().name().contains("PLACE")) {
      plugin.getController().executeZombieLordsChestplate((Player) event.getWhoClicked());
    } else {
      plugin.getController().removeProtectors((Player) event.getWhoClicked());
    }
  }

}
