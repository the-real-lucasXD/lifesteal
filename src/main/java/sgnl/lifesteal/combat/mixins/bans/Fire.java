package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;

@Mixin(Entity.class)
public class Fire {

  @SuppressWarnings("all") @Inject(method = "setRemainingFireTicks",at = @At("HEAD"),cancellable = true)
  private void setRemainingFireTicks(int remainingTicks, CallbackInfo ci) {
    try {
      if ((Entity) (Object) this instanceof ServerPlayer victimServerPlayer) {
        Entity attacker = victimServerPlayer.getLastHurtByMob();
        if (attacker instanceof ServerPlayer) ci.cancel();
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in Fire.java/setRemainingFireTicks", e);
    }
  }
}
