package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.player.Player;

@Mixin(ChestBlock.class)
public class OpeningChests {
  @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
  private void useWithoutItem(BlockState state, Level level, BlockPos pos,
                              net.minecraft.world.entity.player.Player player,
                              BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    if (player instanceof ServerPlayer serverPlayer) {
      Player customPlayer = Config.getPlayer(serverPlayer);
      if (customPlayer.inCombat()) {
        serverPlayer.sendSystemMessage(Component.literal(
          "You cannot open chests while in combat!"
        ).withColor(TextColor.RED), true);
        cir.setReturnValue(InteractionResult.FAIL);
      }
    }
  }
}
