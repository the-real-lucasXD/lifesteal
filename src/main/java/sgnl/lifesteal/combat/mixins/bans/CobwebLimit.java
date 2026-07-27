package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.player.Player;

@Mixin(BlockItem.class)
public abstract class CobwebLimit {
  @Inject(method = "place", at = @At("HEAD"), cancellable = true)
  private void place(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir) {
    if (((BlockItem) (Object) this).getBlock() != Blocks.COBWEB) return;
    if (placeContext.getPlayer() instanceof ServerPlayer serverPlayer) {
      Player player = Config.getPlayer(serverPlayer);
      if (!player.inCombat()) return;
      if (player.combat.cobwebUsed >= Config.INSTANCE.cobwebsPerCombat) {
        cir.setReturnValue(InteractionResult.FAIL);
        player.serverPlayer().sendSystemMessage(Component.literal(
          "You have already used " + Config.INSTANCE.cobwebsPerCombat + " cobwebs!"
        ).withColor(TextColor.RED), true);
        player.queueInventoryRefresh = true;
      } else {
        player.combat.cobwebUsed ++;
        serverPlayer.sendSystemMessage(Component.literal(String.format(
          "Cobwebs used: %d/%d", player.combat.cobwebUsed, Config.INSTANCE.cobwebsPerCombat
        )), true);
      }
    }
  }
}
