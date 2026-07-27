package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.player.Player;

@Mixin(ExperienceBottleItem.class)
public class MendingLimit {
  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  public void use(Level level, net.minecraft.world.entity.player.Player player,
                  InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (player instanceof ServerPlayer serverPlayer) {
      Player customPlayer = Config.getPlayer(serverPlayer);
      if (!customPlayer.inCombat()) return;
      if (customPlayer.combat.xpUsed >= Config.INSTANCE.xpPerCombat) {
        cir.setReturnValue(InteractionResult.FAIL);
        customPlayer.serverPlayer().sendSystemMessage(Component.literal(
          "You have already used " + Config.INSTANCE.xpPerCombat + " experience bottles!"
        ).withColor(TextColor.RED), true);
        customPlayer.queueInventoryRefresh = true;
      } else {
        customPlayer.combat.xpUsed ++;
        serverPlayer.sendSystemMessage(Component.literal(String.format(
          "Experience bottles used: %d/%d", customPlayer.combat.xpUsed, Config.INSTANCE.xpPerCombat
        )), true);
      }
    }
  }
}
