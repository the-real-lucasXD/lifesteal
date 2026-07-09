package sgnl.lifesteal.combat.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(ServerPlayer.class)
public class OnPlayerAttack {
  @Inject(method = "hurtServer", at = @At("HEAD"))
  private void hurt(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
    try {
      @SuppressWarnings("all")
      Player victim = Config.getPlayer((ServerPlayer) (Object) this);

      Entity indirectSource = source.getEntity();
      Entity directSource = source.getDirectEntity();

      Player attacker = null;
      if (indirectSource instanceof ServerPlayer serverPlayer) attacker = Config.getPlayer(serverPlayer);
      else if (directSource instanceof ServerPlayer serverPlayer) attacker = Config.getPlayer(serverPlayer);

      if (attacker != null && !attacker.equals(victim)) {
        victim.enterCombat(attacker);
        attacker.enterCombat(victim);
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnPlayerAttack.java/hurt", e);
    }
  }
}
