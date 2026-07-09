package sgnl.lifesteal.combat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.player.Player;

import java.util.UUID;

public class Combat {
  public int ticksLeft;
  public int pearlsUsed = 0;
  public ServerBossEvent bossBar;
  public Player player;
  public Player attacker;
  
  public void reset(Player attacker) {
    this.attacker = attacker;
    ticksLeft = Config.INSTANCE.combatDuration+1;
    bossBar.setProgress(1);
    tick();
  }
  
  public void clear() {
    bossBar.removeAllPlayers();
    player.combat = null;
  }
  
  public void tick() {
    ticksLeft--;
    bossBar.setProgress((float) ticksLeft / Config.INSTANCE.combatDuration);
    bossBar.setName(Component.literal(String.format("Combat: %.1fs", (double) ticksLeft / 20)));
    if (ticksLeft <= 0) clear();
  }
  
  public Combat(Player player, Player attacker) {
    this.player = player;
    ticksLeft = Config.INSTANCE.combatDuration;
    bossBar = new ServerBossEvent(
      UUID.randomUUID(),
      Component.literal(String.format("Combat: %.1fs", (double) ticksLeft / 20)),
      BossEvent.BossBarColor.RED,
      BossEvent.BossBarOverlay.PROGRESS
    ); bossBar.setProgress(1);
    bossBar.addPlayer(player.serverPlayer());
    reset(attacker);
  }

  public static void clearAll() {
    for (Player i:Config.INSTANCE.players.values()) {
      if (i.online && i.inCombat()) i.combat.clear();
    }
  }
}
