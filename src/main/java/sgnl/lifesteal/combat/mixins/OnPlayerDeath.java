package sgnl.lifesteal.combat.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

import java.util.Objects;

@Mixin(CombatTracker.class)
public class OnPlayerDeath {
  @Shadow @Final private LivingEntity mob;
  
  @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
  private void getDeathMessage(CallbackInfoReturnable<Component> cir) {
    try {
      if (mob instanceof ServerPlayer serverPlayer) {
        Player player = Config.getPlayer(serverPlayer);
        if (player.combatLog) {
          if (player.combat.attacker.inCombat()) {
            cir.setReturnValue(Component.literal(
              String.format("%s combat logged while fighting %s",
                serverPlayer.getScoreboardName(),
                player.combat.attacker.serverPlayer().getScoreboardName()
              )
            ));
            player.combat.attacker.combat.attacker = null;

            if (Objects.requireNonNull(player.combat.attacker).giveHealth(
              1 - player.removeHealth(1), false
            ) == 1) player.dropHeart();
          } else {
            cir.setReturnValue(Component.literal(serverPlayer.getScoreboardName() + " combat logged"));
          }
          player.combatLog = false;
        }
        if (player.inCombat()) {
          player.combat.clear();
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnPlayerDeath.java/getDeathMessage", e);
    }
  }
}
