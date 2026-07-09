package sgnl.lifesteal.player.mixins;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(PlayerList.class)
public class OnPlayerJoin {
  @Inject(method = "placeNewPlayer", at = @At("HEAD"))
  public void placeNewPlayer(Connection connection, ServerPlayer player,
                             CommonListenerCookie cookie, CallbackInfo ci) {
    try {
      Player customPlayer = Config.getPlayer(player);
      customPlayer.online = true;
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnPlayerJoin.java/placeNewPlayer", e);
    }
  }
}
