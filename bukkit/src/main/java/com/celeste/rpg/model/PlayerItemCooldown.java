package com.celeste.rpg.model;

import com.celeste.library.core.model.registry.Registry;
import com.celeste.library.core.model.registry.impl.LinkedRegistry;
import com.celeste.rpg.model.type.ItemsType;
import lombok.Data;

import java.util.UUID;

@Data
public final class PlayerItemCooldown {

  private final UUID id;
  private final Registry<ItemsType, Long> cooldown;

  public PlayerItemCooldown(final UUID id) {
    this.id = id;
    this.cooldown = new LinkedRegistry<>();
  }

  public boolean isInCooldown(final ItemsType type) {
    if (!cooldown.contains(type)) {
      return false;
    }

    if (cooldown.get(type) < System.currentTimeMillis()) {
      cooldown.remove(type);
      return false;
    }

    return true;
  }

  public void clearExpired() {
    cooldown.forEach((type, longValue) -> {
      if (longValue < System.currentTimeMillis()) {
        cooldown.remove(type);
      }
    });
  }

}
