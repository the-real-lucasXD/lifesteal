package sgnl.lifesteal.mixins;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sgnl.lifesteal.Lifesteal;

@Mixin(ItemStack.class)
public class OnMaceEnchant {
  @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true) @SuppressWarnings("all")
  private void isEnchantable(CallbackInfoReturnable<Boolean> cir) {
    try {
      if (((ItemStack) (Object) this).getItem() == Items.MACE) cir.setReturnValue(false);
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnMaceEnchant.java/isEnchantable", e);
    }
  }
}
