package sgnl.lifesteal.heart.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;

import java.util.Objects;

@Mixin(Item.class)
public class OnRightClick {
  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void use(Level level, Player player, InteractionHand hand,
                            CallbackInfoReturnable<InteractionResult> cir) {
    try {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.has(DataComponents.CUSTOM_DATA)) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (Objects.requireNonNull(customData).copyTag().contains("lifesteal_heart") && !level.isClientSide()) {
          sgnl.lifesteal.player.Player customPlayer = Config.getPlayer((ServerPlayer) player);
          boolean success = customPlayer.giveHealth(1, false) == 0;
          if (success) {
            ((ServerPlayer) player).sendSystemMessage(
              Component.literal("Heart successfully applied.").withColor(TextColor.GREEN),
              true
            );
            itemStack.shrink(1);
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
          } else {
            ((ServerPlayer) player).sendSystemMessage(
              Component.literal(String.format("You are already on %d hearts!", Config.maxHealth()))
                .withColor(TextColor.RED),
              true
            );
            cir.setReturnValue(InteractionResult.FAIL);
          }
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnRightClick.java/use", e);
    }
  }
}
