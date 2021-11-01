package com.celeste.rpg.views.listeners;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.controller.ItemsController;
import com.celeste.rpg.model.PlayerItemCooldown;
import com.celeste.rpg.model.type.ItemsType;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
public final class InteractListener implements Listener {

  private final ItemsPlugin plugin;

  @EventHandler
  public void onInteract(final PlayerInteractEvent event) {
    if (!event.hasItem() || !event.getAction().name().contains("RIGHT")) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null || !item.hasItemMeta()) {
      return;
    }

    final AtomicReference<ItemsType> rawType = new AtomicReference<>();
    plugin.getFactory().getItems().forEach((itemType, itemStack) -> {
      if (itemStack.isSimilar(item)) {
        rawType.set(itemType);
      }
    });

    final ItemsType type = ItemsType.get(rawType.get().name());
    if (type == null) {
      return;
    }

    final Player player = event.getPlayer();

    final PlayerItemCooldown itemCooldown = plugin.getFactory().getPlayerCooldown().get(player.getUniqueId());
    if (itemCooldown == null) {
      return;
    }

    if (itemCooldown.isInCooldown(type)) {
      player.sendMessage("§cYou are in cooldown! Wait some seconds to use it again.");
      return;
    }

    final ItemsController controller = plugin.getController();
    switch (type) {
      case WATCH_OF_TIME -> controller.executeWatchOfTime(player);
      case NON_ACTIVATED_DIVINE_SWORD -> controller.executeDivineSword(player, item);
      case THE_DIVINE_SWORD -> controller.executeDivineSwordSpecial(player);
      case ETERNAL_KNIFE -> controller.executeEternalKnife(player);
      case ATOMIC_OBLITERATION -> controller.executeAtomicObliteration(player);
      case CRYSTAL_OF_SPACE -> controller.executeCrystalOfSpace(player);
      case THE_CONCH_OF_REALITY -> controller.executeTheConchOfReality(player);
      case MINECART_JUSTICE -> controller.executeMinecartOfJustice(player);
    }
  }

  @EventHandler
  public void onHeld(final PlayerItemHeldEvent event) {
    final ItemStack item = event.getPlayer().getItemInUse();
    if (item == null || !item.hasItemMeta()) {
      return;
    }

    final AtomicReference<ItemsType> rawType = new AtomicReference<>();
    plugin.getFactory().getItems().forEach((itemType, itemStack) -> {
      if (itemStack.isSimilar(item)) {
        rawType.set(itemType);
      }
    });

    final ItemsType type = ItemsType.get(rawType.get().name());
    if (type != ItemsType.THE_DIVINE_SWORD) {
      return;
    }

    plugin.getController().addToSwordCooldown(event.getPlayer());
  }

}
