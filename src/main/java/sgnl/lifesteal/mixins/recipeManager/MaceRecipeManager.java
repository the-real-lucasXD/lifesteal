package sgnl.lifesteal.mixins.recipeManager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Lifesteal;

@Mixin(CraftingMenu.class)
public class MaceRecipeManager {

  @Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
  private static void slotChangedCraftingGrid(
    AbstractContainerMenu menu,
    ServerLevel level,
    Player player,
    CraftingContainer container,
    ResultContainer resultSlots,
    @Nullable RecipeHolder<CraftingRecipe> recipeHint,
    CallbackInfo ci
  ) {
    try {
      if (!resultSlots.getItem(0).is(Items.MACE)) return;
      int fragCount = 0;
      for (int i = 0; i < container.getContainerSize(); i++) {
        ItemStack stack = container.getItem(i);
        if (stack.isEmpty()) continue;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.copyTag().contains("lifesteal_heavy_fragment")) fragCount++;
      } if (fragCount != 2) {
        resultSlots.setItem(0, ItemStack.EMPTY);
        menu.broadcastChanges();
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in MaceRecipeManager.java/slotChangedCraftingGrid", e);
    }
  }
}
