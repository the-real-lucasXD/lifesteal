package sgnl.lifesteal.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(ServerPlayer.class)
public class OnSystemMessage {
  @SuppressWarnings("all") @Inject(method = "sendSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"))
  private void sendSystemMessage(Component message, boolean overlay, CallbackInfo ci) {
    try {
      if (overlay) {
        Player player = Config.getPlayer((ServerPlayer) (Object) this);
        player.lastSystemMessage = 0;
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnSysyemMessage.java/sendSystemMessage", e);
    }
  }
}
