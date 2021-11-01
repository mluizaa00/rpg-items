package com.celeste.rpg.task;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.controller.ItemsController;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public final class ItemCooldownTask implements Callable<CompletableFuture<Void>> {

  private ItemsPlugin plugin;

  @Override
  public CompletableFuture<Void> call() {
    return CompletableFuture.runAsync(() -> {
      plugin.getFactory().getPlayerCooldown().forEach((uuid, cooldown) -> cooldown.clearExpired());

      final ItemsController controller = plugin.getController();
      plugin.getFactory().getDivineSwordCooldown().forEach((id, cooldown) -> {
        if (controller.isInSwordCooldown(id)) {
          return;
        }

        final Player player = Bukkit.getPlayer(id);
        if (player == null) {
          return;
        }

        controller.disableDivineSword(player);
      });

      if (!plugin.getController().isInWatchOfTime()) {
        return;
      }

      final World world = Bukkit.getWorld("world");
      if (world == null) {
        return;
      }

      world.setTime(12000);
    });
  }

}
