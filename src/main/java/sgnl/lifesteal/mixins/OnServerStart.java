package sgnl.lifesteal.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;

@Mixin(MinecraftServer.class)
public class OnServerStart {
  @SuppressWarnings("all") @Inject(method = "runServer", at = @At("HEAD"))
  private void runServer(CallbackInfo ci) {
    try {
      Lifesteal.server = (MinecraftServer) (Object) this;
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnServerStart.java/runServer", e);
    }
  }
}
