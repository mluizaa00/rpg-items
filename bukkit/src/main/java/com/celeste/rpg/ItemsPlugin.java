package com.celeste.rpg;

import com.celeste.configuration.factory.ConfigurationFactory;
import com.celeste.configuration.model.entity.ReplaceValue;
import com.celeste.configuration.model.entity.type.ReplaceType;
import com.celeste.configuration.model.provider.Configuration;
import com.celeste.configuration.model.type.ConfigurationType;
import com.celeste.library.spigot.AbstractBukkitPlugin;
import com.celeste.rpg.controller.EntityController;
import com.celeste.rpg.controller.ItemsController;
import com.celeste.rpg.factory.ItemsFactory;
import com.celeste.rpg.views.commands.GiveItemCommand;
import com.celeste.rpg.views.listeners.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public final class ItemsPlugin extends AbstractBukkitPlugin {

  private Configuration configuration;

  private ItemsFactory factory;
  private ItemsController controller;
  private EntityController entityController;

  @Override @SneakyThrows
  public void onEnable() {
    this.configuration = ConfigurationFactory.getInstance().start(
        ConfigurationType.YAML,
        getDataFolder().getAbsolutePath(),
        "settings.yml",
        false
    );

    final ReplaceValue value = ReplaceValue.builder()
        .value("§")
        .type(ReplaceType.ALL)
        .build();

    configuration.getReplacer().put("&", value);

    this.factory = new ItemsFactory(this);
    this.controller = new ItemsController(this);
    this.entityController = new EntityController();

    registerListeners(
        new ElytraListener(this), new InteractListener(this),
        new PlayerListener(this), new WatchOfTimeListener(this),
        new ProtectorListener(this), new CrystalOfSpaceListener(this)
    );

    registerCommands(
        new GiveItemCommand(this)
    );

    getScheduled().scheduleWithFixedDelay(() -> factory.getCooldownTask().call(), 5, 1, TimeUnit.SECONDS);
    Bukkit.getScheduler().runTaskTimer(this, () -> factory.getProtectorTask().call(), 20, 5 * 20);
  }

  @Override
  public void onDisable() {
    shutdownExecutors();

    for (List<Entity> entities : factory.getProtectors().getAll()) {
      entities.forEach(Entity::remove);
    }
  }

}
