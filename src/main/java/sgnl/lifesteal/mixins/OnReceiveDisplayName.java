package sgnl.lifesteal.mixins;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;

@Mixin(PlayerList.class)
public class OnReceiveDisplayName {
  @SuppressWarnings("all") @Inject(
    method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
    at = @At("HEAD"), cancellable = true
  ) private void broadcastChatMessage(PlayerChatMessage message, ServerPlayer sender, ChatType.Bound chatType, CallbackInfo ci) {
    try {
      if (sender.isInvisible()) {
        ((PlayerList) (Object) this).broadcastSystemMessage(
          Component.literal("<§kREDACTED§r> " + message.signedContent()), false
        );
        ci.cancel();
      }
    } catch(Exception e) {
      Lifesteal.LOGGER.error("Error in OnReceiveDisplayName.java/broadcastChatMessage", e);
    }
  }
}