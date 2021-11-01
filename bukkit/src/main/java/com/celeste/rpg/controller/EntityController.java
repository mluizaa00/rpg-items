package com.celeste.rpg.controller;

import org.bukkit.Location;
import org.bukkit.entity.*;

public final class EntityController {

  public void track(final Entity entity, final Player player) {
    if (entity == null) {
      return;
    }

    final Location location = player.getLocation();
    double distance = location.distance(entity.getLocation());
    if (player.isFlying() || distance < 5) {
      return;
    }

    if (distance > 32) {
      entity.teleport(location);
    }
  }

}
