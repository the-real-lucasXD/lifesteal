package sgnl.lifesteal.combat.mixins;

import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(ServerGamePacketListenerImpl.class)
public class OnPlayerLeave {
  @Inject(method = "onDisconnect", at = @At("HEAD"))
  private void onDisconnect(DisconnectionDetails details, CallbackInfo ci) {
    try {
      @SuppressWarnings("all")
      ServerPlayer serverPlayer = ((ServerGamePacketListenerImpl) (Object) this).player;
      Player player = Config.getPlayer(serverPlayer);
      if (player.inCombat()) {
        player.combatLog = true;
        serverPlayer.setHealth(0);
        serverPlayer.die(serverPlayer.damageSources().genericKill());
      }
      player.online = false;
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnPlayerLeave.java/onDisconnect", e);
    }
  }
}
