package sgnl.lifesteal.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.combat.Combat;

@Mixin(MinecraftServer.class)
public class OnWorldSave {
  @Inject(method = "saveEverything", at = @At("HEAD"))
  private void onWorldSave(boolean silent, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
    try {
      Config.save();
      Lifesteal.LOGGER.info("Saved configs");
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error while saving configs:", e);
    }
  }
  
  @Inject(method = "stopServer", at = @At("HEAD"))
  private void onWorldClose(CallbackInfo ci) {
    try {
      Config.save();
      Combat.clearAll();
      Lifesteal.LOGGER.info("SERVER CLOSING: Saved configs");
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error while saving configs:", e);
    }
  }
}
