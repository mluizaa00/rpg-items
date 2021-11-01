package com.celeste.rpg.factory;

import com.celeste.configuration.model.provider.Configuration;
import com.celeste.library.core.model.registry.Registry;
import com.celeste.library.core.model.registry.impl.ConcurrentRegistry;
import com.celeste.library.core.model.registry.impl.LinkedRegistry;
import com.celeste.library.core.model.registry.impl.WeakRegistry;
import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.model.type.ItemsType;
import com.celeste.rpg.model.PlayerItemCooldown;
import com.celeste.rpg.task.ItemCooldownTask;
import com.celeste.rpg.task.ProtectorsTrackTask;
import com.celeste.rpg.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@Getter
public final class ItemsFactory {

  private final Registry<ItemsType, ItemStack> items;
  private final Registry<UUID, PlayerItemCooldown> playerCooldown;
  private final Registry<Player, List<Entity>> protectors;
  private final Registry<UUID, Long> divineSwordCooldown;
  private final Registry<Location, Player> portals;

  private final ItemCooldownTask cooldownTask;
  private final ProtectorsTrackTask protectorTask;

  public ItemsFactory(final ItemsPlugin plugin) {
    this.items = new LinkedRegistry<>();
    this.playerCooldown = new WeakRegistry<>();
    this.protectors = new ConcurrentRegistry<>();
    this.divineSwordCooldown = new ConcurrentRegistry<>();
    this.portals = new ConcurrentRegistry<>();

    this.cooldownTask = new ItemCooldownTask(plugin);
    this.protectorTask = new ProtectorsTrackTask(plugin);

    load(plugin.getConfiguration());
  }

  private void load(final Configuration config) {
    config.getKeys("items").forEach(key -> {
      final String path = "items." + key + ".";

      final ItemsType type = ItemsType.get(config.getString(path + "type"));
      if (type == null) {
        return;
      }

      final ItemStack item = new ItemBuilder(Material.valueOf(config.getString(path + "material"))).build();

      final ItemMeta meta = item.getItemMeta();
      if (meta == null) {
        return;
      }

      meta.setDisplayName(config.getString(path + "name"));
      meta.setLore(config.getStringList(path + "lore"));
      meta.setUnbreakable(type == ItemsType.WINGS_OF_ETERNAL_FLIGHT);
      item.setItemMeta(meta);

      items.register(type, item);
    });
  }

}
