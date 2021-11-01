package com.celeste.rpg.task;

import com.celeste.rpg.ItemsPlugin;
import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;

@AllArgsConstructor
public final class ProtectorsTrackTask implements Callable<Void> {

  private final ItemsPlugin plugin;

  @Override
  public Void call() {
    plugin.getFactory().getProtectors().forEach((player, entities) -> entities.forEach(entity -> plugin.getEntityController().track(entity, player)));
    return null;
  }

}
