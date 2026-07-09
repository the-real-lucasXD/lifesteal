package sgnl.lifesteal.player;

import net.minecraft.server.level.ServerPlayer;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.combat.Combat;
import sgnl.lifesteal.heart.HeartManager;

import java.util.UUID;

public class Player implements HeartManager {
  public int health = 10;
  public String uuid;

  public transient Combat combat = null;
  public transient boolean combatLog = false;
  public transient boolean online = false;
  public transient boolean cancelAttack = false;
  public transient boolean firstCombatHit = false;
  public transient boolean queueInventoryRefresh = false;
  public transient int lastSystemMessage = 60;

  public Player(String uuid) { this.uuid = uuid; }
  
  @Override public int health() { return health; }
  @Override public UUID uuid() { return UUID.fromString(uuid); }
  @Override public ServerPlayer serverPlayer() { return Lifesteal.server.getPlayerList().getPlayer(uuid()); }
  @Override public boolean inCombat() { return combat != null; }
  @Override public void setHealth(int amount) { this.health = amount; }
  
  public void enterCombat(Player attacker) {
    if (!inCombat()) {
      firstCombatHit = true;
      combat = new Combat(this, attacker);
    } else {
      firstCombatHit = false;
      combat.reset(attacker);
    }
  }
}
