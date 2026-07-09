package sgnl.lifesteal.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.VoteStop;

@Mixin(MinecraftServer.class)
public class OnGameTick {
  @Inject(method = "tickServer", at = @At("HEAD"))
  private void tickServer(CallbackInfo ci) {
    try {
      VoteStop.check();
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnGameTick.java/tickServer", e);
    }
  }
}
