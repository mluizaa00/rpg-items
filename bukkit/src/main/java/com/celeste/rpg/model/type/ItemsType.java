package com.celeste.rpg.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.StringJoiner;

@Getter
@AllArgsConstructor
public enum ItemsType {

  WATCH_OF_TIME(11),
  CRYSTAL_OF_SPACE(60),
  THE_DIVINE_SWORD(30),
  NON_ACTIVATED_DIVINE_SWORD(0),
  KEY_OF_THE_DIVINE(0),
  MINECART_JUSTICE(60),
  ZOMBIE_LORDS_CHESTPLATE(0),
  ETERNAL_KNIFE(10),
  ATOMIC_OBLITERATION(30),
  WINGS_OF_ETERNAL_FLIGHT(0),
  THE_CONCH_OF_REALITY(60 * 10);

  private final int cooldownTime;

  public static ItemsType get(final String name) {
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null);
  }

  public static String getStringList() {
    final StringJoiner joiner = new StringJoiner(", ");
    for (ItemsType value : values()) {
      joiner.add(value.name());
    }

    return joiner.toString();
  }

}
