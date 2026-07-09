package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(EnderpearlItem.class)
public class EnderPearlLimit {
  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void enderPearlLimit(Level level, net.minecraft.world.entity.player.Player player,
                                  InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    try {
      if (player instanceof ServerPlayer serverPlayer) {
        Player customPlayer = Config.getPlayer(serverPlayer);
        if (customPlayer.inCombat()) {
          if (customPlayer.combat.pearlsUsed >= Config.INSTANCE.enderPearlsPerCombat) {
            serverPlayer.sendSystemMessage(
              Component.literal("You have already used " + Config.INSTANCE.enderPearlsPerCombat + " ender pearls!")
                .withColor(TextColor.RED),
              true
            );
            cir.setReturnValue(InteractionResult.FAIL);
            customPlayer.queueInventoryRefresh = true;
          }
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in EnderPearlLimit.java/enderPearlLimit", e);
    }
  }

  @Inject(method = "use", at = @At("RETURN"), cancellable = true)
  private void use(Level level, net.minecraft.world.entity.player.Player player,
                   InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    try {
      if (player instanceof ServerPlayer serverPlayer) {
        Player customPlayer = Config.getPlayer(serverPlayer);
        if (customPlayer.inCombat()) {
          if (player.getCooldowns().isOnCooldown(Items.ENDER_PEARL.getDefaultInstance())) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
          }
          customPlayer.combat.pearlsUsed++;
          serverPlayer.sendSystemMessage(
            Component.literal(String.format("Pearls used: %d/%d", customPlayer.combat.pearlsUsed, Config.INSTANCE.enderPearlsPerCombat)),
            true
          );
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in EnderPearlLimit.java/use", e);
    }
  }
}
