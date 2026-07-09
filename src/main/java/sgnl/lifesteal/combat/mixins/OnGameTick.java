package sgnl.lifesteal.combat.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(MinecraftServer.class)
public class OnGameTick {
  @Inject(method = "tickServer", at = @At("HEAD"))
  private void tickServer(CallbackInfo ci) {
    try {
      for (Player player : Config.INSTANCE.players.values()) {
        if (player.inCombat()) {
          player.combat.tick();
          if (player.serverPlayer().isFallFlying()) player.serverPlayer().stopFallFlying();
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnGameTick.java/tickServer", e);
    }
  }
}
