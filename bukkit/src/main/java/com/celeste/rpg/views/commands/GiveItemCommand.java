package com.celeste.rpg.views.commands;

import com.celeste.rpg.ItemsPlugin;
import com.celeste.rpg.model.type.ItemsType;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public final class GiveItemCommand {

  private final ItemsPlugin plugin;

  @Command(
      name = "rpgitem",
      permission = "rpgitems.give",
      target = CommandTarget.ALL,
      usage = "rpgitem <item_name> <player> <amount>"
  )
  public void handleCommand(final Context<CommandSender> context, final String rawItem, final String rawPlayer, final int amount) {
    final ItemsType type = ItemsType.get(rawItem);
    if (type == null) {
      context.sendMessage("§cYou sent a invalid type! Use the followings: §e" + ItemsType.getStringList());
      return;
    }

    final Player player = Bukkit.getPlayer(rawPlayer);
    if (player == null || !player.isOnline()) {
      context.sendMessage("§cYou sent a invalid player!");
      return;
    }

    final ItemStack item = plugin.getFactory().getItems().get(type);
    if (item == null) {
      return;
    }

    item.setAmount(amount);
    if (player.getInventory().firstEmpty() == -1) {
      player.getWorld().dropItemNaturally(player.getLocation(), item);
    } else {
      player.getInventory().addItem(item);
    }
  }

}
