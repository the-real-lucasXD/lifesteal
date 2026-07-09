package sgnl.lifesteal.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;

@Mixin(AnvilMenu.class)
public abstract class OnMaceAnvilUse extends ItemCombinerMenu {
  @Shadow @Final private DataSlot cost;

  @SuppressWarnings("all")
  public OnMaceAnvilUse() {
    super(null, 0, null, null, null);
  }

  @Inject(method = "createResult", at = @At("TAIL"))
  private void createResult(CallbackInfo ci) {
    try {
      ItemStack target = this.inputSlots.getItem(0);
      ItemStack output = this.resultSlots.getItem(0);

      if (target.is(Items.MACE) && !output.isEmpty()) {
        ItemEnchantments current = output.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable filtered = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        boolean kept = false;

        for (Holder<Enchantment> holder : current.keySet()) {
          if (holder.unwrapKey().map(key -> key.identifier().toString())
            .orElse("").equals("minecraft:wind_burst")) {
            filtered.set(holder, 1);
            kept = true;
          }
          output.set(DataComponents.ENCHANTMENTS, filtered.toImmutable());

          ItemEnchantments original = target.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
          if (!kept && original.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
          }
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in OnMaceAnvilUse.java/createResult", e);
    }
  }
}
