package sgnl.lifesteal.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.VoteStop;

@Mixin(ServerCommonPacketListenerImpl.class)
public class OnVoteStopButton {
  @Inject(method = "handleCustomClickAction", at = @At("HEAD"))
  private void handleCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
    try {
      if (!Lifesteal.server.isSameThread()) return;
      if (packet.id().equals(Identifier.tryBuild("lifesteal", "serverstopvote"))) {
        if ((Object) this instanceof ServerGamePacketListenerImpl gamePacketListener) {
          VoteStop.voted.add(gamePacketListener.player.getStringUUID());
          Lifesteal.server.getPlayerList().broadcastSystemMessage(
            Component.literal(
              String.format("%s has voted to stop the server! ", gamePacketListener.player.getScoreboardName())
            ).copy().append(
              Component.literal(String.format("(%d of %d)", VoteStop.voted.size(), VoteStop.requirement))
                .withColor(TextColor.GRAY)
            ),
            false
          );
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnVoteStopButton.java/handleCustomClickAction", e);
    }
  }
}
