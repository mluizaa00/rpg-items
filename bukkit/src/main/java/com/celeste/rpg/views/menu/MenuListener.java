package com.celeste.rpg.views.menu;

import com.celeste.rpg.ItemsPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public final class MenuListener implements Listener {

  private final ItemsPlugin plugin;

  @EventHandler
  public void onClick(final InventoryClickEvent event) {
    event.setCancelled(true);

    final Player player = (Player) event.getWhoClicked();
    player.closeInventory();

    final Inventory inventory = event.getClickedInventory();
    if (inventory == null) {
      return;
    }

    System.out.println("TRY TITLE");
    if (!event.getView().getTitle().equalsIgnoreCase("Select player to create portal")) {
      return;
    }
    System.out.println("PASSED TITLE");

    final ItemStack item = event.getCurrentItem();
    if (item == null || item.getItemMeta() == null) {
      return;
    }

    final Player target = Bukkit.getPlayer(item.getItemMeta().getDisplayName()
        .replace("§", "")
        .replace("&", "")
    );

    if (target == null) {
      return;
    }

    System.out.println("PASSED TARGET");

    executeTarget((Player) event.getWhoClicked(), target);
  }

  private void executeTarget(final Player player, final Player target) {
    player.setMetadata("portals", new FixedMetadataValue(plugin, "yes"));
    final List<Location> results = new ArrayList<>();

    final Location location = player.getLocation().clone().add(0, 1, 0);
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    plugin.getFactory().getPortals().put(location, target);

    double minX = location.getBlockX();
    double minY = location.getBlockY();
    double minZ = location.getBlockZ();
    double maxX = location.getBlockX() + 1;
    double maxY = location.getBlockY() + 1;
    double maxZ = location.getBlockZ() + 1;

    for (double x = minX; x <= maxX; x += 2) {
      for (double y = minY; y <= maxY; y += 2) {
        for (double z = minZ; z <= maxZ; z += 2) {
          int components = 0;
          if (x == minX || x == maxX) {
            components++;
          }

          if (y == minY || y == maxY) {
            components++;
          }

          if (z == minZ || z == maxZ) {
            components++;
          }

          if (components >= 2) {
            results.add(new Location(world, x, y, z));
          }
        }
      }
    }

    for (Location location1 : results) {
      world.spawnParticle(Particle.PORTAL, location1,16, 1.2F, 0F, 1.2F);
    }
  }

}
