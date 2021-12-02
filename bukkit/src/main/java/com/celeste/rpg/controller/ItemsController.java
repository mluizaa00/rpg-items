package com.celeste.rpg.controller;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.factory.ItemsFactory;
import com.celeste.rpg.model.PlayerItemCooldown;
import com.celeste.rpg.model.type.ItemsType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bukkit.entity.EntityType.*;

@Getter
@RequiredArgsConstructor
public final class ItemsController {

  private static final Random RANDOM;
  private static final EntityType[] MOBS;

  static {
    RANDOM = new Random();
    MOBS = new EntityType[] {WITHER_SKELETON, CREEPER, SKELETON, SPIDER, GIANT,
    ZOMBIE, CAVE_SPIDER, IRON_GOLEM};
  }

  private final ItemsPlugin plugin;

  private boolean inWatchOfTime;
  private UUID usedWatchOfTime;

  public void addCooldown(final ItemsType type, final Player player) {
    final PlayerItemCooldown itemCooldown = plugin.getFactory().getPlayerCooldown().get(player.getUniqueId());
    if (itemCooldown == null) {
      return;
    }

    itemCooldown.getCooldown().register(type, System.currentTimeMillis() + (type.getCooldownTime() * 1000L));
  }

  public void executeWatchOfTime(final Player player) {
    this.inWatchOfTime = true;
    this.usedWatchOfTime = player.getUniqueId();

    final PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, 11, 1);
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer.getUniqueId() == player.getUniqueId()) {
        continue;
      }

      onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_ILLUSIONER_HURT, 2, 1);
      onlinePlayer.addPotionEffect(effect);
      onlinePlayer.sendTitle("§cTime has Stopped!", "", 80, 20 ,80);
    }

    addCooldown(ItemsType.WATCH_OF_TIME, player);

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      this.inWatchOfTime = false;
      this.usedWatchOfTime = null;

      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 2, 1);
      }

      addCooldown(ItemsType.WATCH_OF_TIME, player);
    }, 20 * 11);
  }

  public void executeDivineSword(final Player player, final ItemStack sword) {
    final PlayerInventory inventory = player.getInventory();

    final ItemStack originalKey = plugin.getFactory().getItems().get(ItemsType.KEY_OF_THE_DIVINE);
    if (originalKey == null) {
      return;
    }

    final ItemStack key = Arrays.stream(inventory.getContents())
        .filter(itemStack -> itemStack.isSimilar(originalKey))
        .findFirst()
        .orElse(null);

    if (key == null) {
      player.sendMessage("§cYou don't have the Divine Sword key!");
      return;
    }

    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

    final ItemStack old = plugin.getFactory().getItems().get(ItemsType.THE_DIVINE_SWORD);
    if (old == null) {
      return;
    }

    inventory.remove(sword);
    inventory.setItem(inventory.getHeldItemSlot(), old);

    addToSwordCooldown(player);
  }

  public void disableDivineSword(final Player player) {
    final PlayerInventory inventory = player.getInventory();

    final ItemStack old = plugin.getFactory().getItems().get(ItemsType.THE_DIVINE_SWORD);
    if (old == null) {
      return;
    }

    final AtomicInteger integer = new AtomicInteger(0);
    final ItemStack originalSword = Arrays.stream(inventory.getContents())
        .filter(itemStack -> {
          integer.getAndIncrement();
          return itemStack.isSimilar(old);
        })
        .findFirst()
        .orElse(null);

    if (originalSword == null) {
      return;
    }

    inventory.remove(originalSword);
    final ItemStack newSword = plugin.getFactory().getItems().get(ItemsType.NON_ACTIVATED_DIVINE_SWORD);
    if (newSword == null) {
      return;
    }

    inventory.setItem(integer.get(), newSword);
  }

  public void removeFromRegistry(final Player player) {
    final ItemsFactory factory = plugin.getFactory();

    factory.getProtectors().remove(player);
    factory.getPlayerCooldown().remove(player.getUniqueId());
    factory.getDivineSwordCooldown().remove(player.getUniqueId());
  }

  public void addToSwordCooldown(final Player player) {
    plugin.getFactory().getDivineSwordCooldown().put(player.getUniqueId(), System.currentTimeMillis() + 1000 * 30);
  }

  public boolean isInSwordCooldown(final UUID id) {
    if (!plugin.getFactory().getDivineSwordCooldown().contains(id)) {
      return false;
    }

    final long time = plugin.getFactory().getDivineSwordCooldown().get(id);
    if (time > System.currentTimeMillis()) {
      return true;
    }

    plugin.getFactory().getDivineSwordCooldown().remove(id);
    return false;
  }

  public void executeDivineSwordSpecial(final Player player) {
    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
      player.getWorld().strikeLightning(entity.getLocation());
    }

    addCooldown(ItemsType.THE_DIVINE_SWORD, player);
    addToSwordCooldown(player);
  }

  public void executeEternalKnife(final Player player) {
    final Location location = player.getEyeLocation();
    final Vector direction = location.getDirection();

    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    for (int i = 0; i < 10; i++) {
      final Arrow arrow = world.spawnArrow(location, new Vector(0,0,0), 1, 0);

      arrow.setVelocity(direction.multiply(3));
      arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
      arrow.setGravity(false);
      arrow.setShooter(player);
    }

    addCooldown(ItemsType.ETERNAL_KNIFE, player);
  }

  public void executeMinecartOfJustice(final Player player) {
    final Location location = player.getEyeLocation().clone();
    final World world = player.getWorld();

    player.setInvulnerable(true);
    for (int i = 0; i < 10; i++) {
      double x = RANDOM.doubles(-3D, 3D).findFirst().getAsDouble();
      double z = RANDOM.doubles(-3D, 3D).findFirst().getAsDouble();

      final Entity entity = world.spawnEntity(location.clone().add(x, 8, z), EntityType.MINECART_TNT);

      final EntityMinecartAbstract minecart = (EntityMinecartAbstract) ((CraftEntity) entity).getHandle();
      minecart.setInvulnerable(true);
      minecart.setOnFire(20, false);
    }

    Bukkit.getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), 60);
    addCooldown(ItemsType.MINECART_JUSTICE, player);
  }

  public void executeAtomicObliteration(final Player player) {
    // Explode all near entities
    player.setInvulnerable(true);
    for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
      player.getWorld().createExplosion(entity.getLocation(), 4, false);
    }

    player.setInvulnerable(false);

    addCooldown(ItemsType.ATOMIC_OBLITERATION, player);

    final Location location = player.getLocation();

    // Transform blocks near into Sand
    final Location locationMin = location.clone().subtract(5, 5, 5);
    final Location locationMax = location.clone().add(5, 5, 5);

    for (double x = locationMin.getX(); x <= locationMax.getX(); x++) {
      for (double y = locationMin.getY(); y <= locationMax.getY(); y++) {
        for (double z = locationMin.getZ(); z <= locationMax.getZ(); z++) {
          final Location radiusLocation = new Location(location.getWorld(), x, y, z);
          if (radiusLocation.getBlock().getType() == Material.AIR) {
            continue;
          }

          radiusLocation.getBlock().setType(Material.SAND);
        }
      }
    }
  }

  public void executeTheConchOfReality(final Player player) {
    if (plugin.getFactory().getProtectors().contains(player)) {
      removeProtectors(player);
    }

    saveProtectors(player, List.of(createProtector(player, player.getLocation().clone().add(1, 0, 1), false)));
    addCooldown(ItemsType.THE_CONCH_OF_REALITY, player);
  }

  public void executeZombieLordsChestplate(final Player player) {
    if (plugin.getFactory().getProtectors().contains(player)) {
      removeProtectors(player);
    }

    final Location location = player.getLocation();

    final List<Entity> entityList = new ArrayList<>(10);
    for (int i = 0; i < 10; i++) {
      double x = RANDOM.doubles(-1.5D, 1.5D).findFirst().getAsDouble();
      double z = RANDOM.doubles(-1.5D, 1.5D).findFirst().getAsDouble();

      final Entity entity = createProtector(player, location.clone().add(x, 2, z), true);
      entityList.add(entity);
    }

    saveProtectors(player, entityList);
    addCooldown(ItemsType.THE_CONCH_OF_REALITY, player);

    player.sendMessage("§aYou placed your Zombie Lords Chestplate! 10 zombies spawned around you.");
  }

  public void removeProtectors(final Player player) {
    final List<Entity> entities = plugin.getFactory().getProtectors().get(player);
    if (entities == null) {
      return;
    }

    entities.forEach(entity -> {
      entity.remove();
      plugin.getFactory().getProtectors().remove(player);
    });

    player.sendMessage("§cYour protectors have been removed!");
  }

  private Entity createProtector(final Player player, final Location location, boolean zombie) {
    final Entity entity = player.getWorld().spawnEntity(location, zombie ? ZOMBIE : MOBS[RANDOM.nextInt(MOBS.length)]);

    entity.setCustomName("§aProtector of " + player.getName());
    entity.setCustomNameVisible(true);
    entity.setSilent(true);

    entity.removeMetadata("owner", plugin);
    entity.setMetadata("owner", new FixedMetadataValue(plugin, player.getName()));

    if (entity instanceof Ageable) {
      ((Ageable) entity).setAdult();
    }

    if (entity instanceof Tameable) {
      ((Tameable) entity).setOwner(player);
      ((Tameable) entity).setTamed(true);
    }

    return entity;
  }

  public void saveProtectors(final Player player, final List<Entity> protectors) {
    final List<Entity> entities = plugin.getFactory().getProtectors().get(player);
    if (entities == null) {
      return;
    }

    entities.addAll(protectors);
  }

}
