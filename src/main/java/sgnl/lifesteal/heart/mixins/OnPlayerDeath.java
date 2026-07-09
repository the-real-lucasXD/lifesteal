package sgnl.lifesteal.heart.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

import java.util.Objects;

@Mixin(ServerPlayer.class)
public class OnPlayerDeath {
  @SuppressWarnings("all") @Inject(method = "die", at = @At("HEAD"))
  public void die(DamageSource source, CallbackInfo ci) {
    try {
      Player victim = Config.getPlayer((ServerPlayer) (Object) this);

      Entity indirectSource = source.getEntity();
      Entity directSource = source.getDirectEntity();

      Player attacker = null;
      if (indirectSource instanceof ServerPlayer serverPlayer) attacker = Config.getPlayer(serverPlayer);
      else if (directSource instanceof ServerPlayer serverPlayer) attacker = Config.getPlayer(serverPlayer);

      if (attacker != null) {
        if (Objects.requireNonNull(attacker).giveHealth(
          1 - victim.removeHealth(1), false
        ) == 1) victim.dropHeart();
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnPlayerDeath.java/die", e);
    }
  }
}
