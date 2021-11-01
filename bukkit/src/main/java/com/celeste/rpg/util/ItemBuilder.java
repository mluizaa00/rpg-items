package com.celeste.rpg.util;

import com.celeste.library.core.util.Wrapper;
import com.celeste.library.spigot.util.item.type.EnchantmentType;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import java.util.*;
import java.util.Map.Entry;

public final class ItemBuilder implements Cloneable {

  private final ItemStack itemStack;
  private ItemMeta meta;

  public ItemBuilder(final ItemStack itemStack) {
    this.itemStack = itemStack;
    this.meta = itemStack.getItemMeta();
  }

  public ItemBuilder(final Material material) {
    this(material, 1);
  }

  public ItemBuilder(final Material material, final int amount) {
    this.itemStack = new ItemStack(material, amount);
    this.meta = itemStack.getItemMeta();
  }

  public ItemBuilder material(final Material material) {
    itemStack.setType(material);
    return this;
  }

  public ItemBuilder data(final int data) {
    meta.setCustomModelData(data);
    return this;
  }

  public ItemBuilder unbreakable(final boolean unbreakable) {
    meta.setUnbreakable(unbreakable);
    return this;
  }

  public boolean hasModelData() {
    return meta.hasCustomModelData();
  }

  public ItemBuilder amount(final int amount) {
    itemStack.setAmount(amount);
    return this;
  }

  public ItemBuilder name(final String name) {
    meta.setDisplayName(name);
    return this;
  }

  public ItemBuilder lore(final String... lore) {
    return lore(ImmutableList.copyOf(lore));
  }

  public ItemBuilder lore(final List<String> lore) {
    if (lore.size() == 0) {
      return this;
    }

    meta.setLore(lore);
    return this;
  }

  public ItemBuilder addLore(final String... lore) {
    return addLore(ImmutableList.copyOf(lore));
  }

  public ItemBuilder addLore(final List<String> lore) {
    if (lore.size() == 0) {
      return this;
    }

    final List<String> currentLore = meta.getLore();
    final List<String> newLore = currentLore == null
        ? new ArrayList<>()
        : currentLore;

    newLore.addAll(lore);
    meta.setLore(newLore);
    return this;
  }

  public ItemBuilder removeLore(final String... lore) {
    return removeLore(ImmutableList.copyOf(lore));
  }

  public ItemBuilder removeLore(final List<String> lore) {
    if (lore.size() == 0) {
      return this;
    }

    final List<String> currentLore = meta.getLore();
    final List<String> newLore = currentLore == null
        ? new ArrayList<>()
        : currentLore;

    newLore.removeAll(lore);
    meta.setLore(newLore);
    return this;
  }

  public ItemBuilder removeLoreLine(final int line) {
    final List<String> currentLore = meta.getLore();

    if (currentLore == null || line > currentLore.size()) {
      return this;
    }

    currentLore.remove(line);
    meta.setLore(currentLore);
    return this;
  }

  public ItemBuilder replaceLoreLine(final String lore, final int line) {
    final List<String> currentLore = meta.getLore();
    final List<String> newLore = currentLore == null
        ? new ArrayList<>()
        : currentLore;

    for (int index = newLore.size(); index <= line; index++) {
      newLore.add("§c");
    }

    newLore.set(line, lore);
    meta.setLore(newLore);
    return this;
  }

  public ItemBuilder addEnchantment(final String... enchantments) {
    return addEnchantment(ImmutableList.copyOf(enchantments));
  }

  public ItemBuilder addEnchantment(final Enchantment enchant, final int level) {
    itemStack.addEnchantment(enchant, level);
    return this;
  }

  public ItemBuilder addEnchantment(final List<String> enchantments) {
    if (enchantments.size() == 0) {
      return this;
    }

    for (String enchantment : enchantments) {
      final String[] split = enchantment.split(":");
      if (split.length != 2) {
        continue;
      }

      final Enchantment enchant = EnchantmentType.getRealEnchantment(split[0]);
      itemStack.addUnsafeEnchantment(enchant, Integer.parseInt(split[1]));
    }

    return this;
  }

  public ItemBuilder addEnchantment(final String enchantment, final int level) {
    final Enchantment enchant = EnchantmentType.getRealEnchantment(enchantment);
    itemStack.addUnsafeEnchantment(enchant, level);
    return this;
  }

  @SafeVarargs
  public final ItemBuilder addEnchantment(final Entry<String, Integer>... enchantments) {
    if (enchantments.length == 0) {
      return this;
    }

    Arrays.stream(enchantments).forEach(enchantment -> {
      final Enchantment enchant = EnchantmentType.getRealEnchantment(enchantment.getKey());
      itemStack.addUnsafeEnchantment(enchant, enchantment.getValue());
    });

    return this;
  }

  public ItemBuilder addEnchantment(final Map<String, Integer> enchantments) {
    if (enchantments.size() == 0) {
      return this;
    }

    enchantments.forEach((enchantment, level) -> {
      final Enchantment enchant = EnchantmentType.getRealEnchantment(enchantment);
      itemStack.addUnsafeEnchantment(enchant, level);
    });

    return this;
  }

  public ItemBuilder removeEnchantment(final String... enchantments) {
    return removeEnchantment(ImmutableList.copyOf(enchantments));
  }

  public ItemBuilder removeEnchantment(final List<String> enchantments) {
    if (enchantments.size() == 0) {
      return this;
    }

    enchantments.forEach(enchantment -> {
      final Enchantment enchant = EnchantmentType.getRealEnchantment(enchantment);
      itemStack.removeEnchantment(enchant);
    });

    return this;
  }

  public ItemBuilder glow(final boolean glow) {
    if (glow) {
      itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
      return addItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    if (!itemStack.containsEnchantment(Enchantment.DURABILITY)) {
      return this;
    }

    itemStack.removeEnchantment(Enchantment.DURABILITY);
    return removeItemFlag(ItemFlag.HIDE_ENCHANTS);
  }

  @SneakyThrows
  @SuppressWarnings("deprecation")
  public ItemBuilder skull(final String owner) {
    if (itemStack.getType() != Material.PLAYER_HEAD) {
      return this;
    }

    final SkullMeta skullMeta = (SkullMeta) meta;
    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
    return this;
  }

  public ItemBuilder mob(final EntityType type) {
    if (itemStack.getType() != Material.SPAWNER) {
      return this;
    }

    final BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
    final BlockState state = blockStateMeta.getBlockState();

    ((CreatureSpawner) state).setSpawnedType(type);
    blockStateMeta.setBlockState(state);
    return this;
  }

  public ItemBuilder armor(final Color color) {
    if (!Wrapper.isObject(meta, LeatherArmorMeta.class)) {
      return this;
    }

    ((LeatherArmorMeta) meta).setColor(color);
    return this;
  }

  public ItemBuilder addItemFlag(final ItemFlag... flags) {
    meta.addItemFlags(flags);
    return this;
  }

  public ItemBuilder removeItemFlag(final ItemFlag... flags) {
    meta.removeItemFlags(flags);
    return this;
  }

  public ItemStack build() {
    itemStack.setItemMeta(meta);
    return itemStack;
  }

  @Override
  @SneakyThrows
  public ItemBuilder clone() {
    return (ItemBuilder) super.clone();
  }

}
