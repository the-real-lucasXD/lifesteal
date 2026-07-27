package sgnl.lifesteal.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VaultServerData.class)
public class InfiniteVaults {
  @Inject(method = "addToRewardedPlayers", at = @At("HEAD"), cancellable = true)
  private void addToRewardedPlayers(Player player, CallbackInfo ci) {
    ci.cancel();
  }
}
