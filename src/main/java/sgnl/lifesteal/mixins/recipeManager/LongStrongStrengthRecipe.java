package sgnl.lifesteal.mixins.recipeManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(BrewingStandBlockEntity.class)
public class LongStrongStrengthRecipe {
  
  @Unique
  private static void setFinalStrength(ItemStack bottle, NonNullList<ItemStack> items, int i) {
    ItemStack newBottle = new ItemStack(bottle.getItem());
    
    MobEffectInstance effect = new MobEffectInstance(MobEffects.STRENGTH, 9600, 1);
    PotionContents newContents = new PotionContents(
      Optional.empty(),
      Optional.of(MobEffects.STRENGTH.value().getColor()),
      List.of(effect),
      Optional.of("strength")
    );
    
    newBottle.set(DataComponents.POTION_CONTENTS, newContents);
    items.set(i, newBottle);
  }
  
  @Inject(method = "isBrewable", at = @At("HEAD"), cancellable = true)
  private static void isBrewable(PotionBrewing potionBrewing, NonNullList<ItemStack> items,
                                 CallbackInfoReturnable<Boolean> cir) {
    ItemStack ingredient = items.get(3);
    if (!ingredient.isEmpty()) {
      for (int i=0; i<3; i++) {
        ItemStack bottle = items.get(i);
        if (bottle.isEmpty()) continue;
        
        PotionContents contents = bottle.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isPresent()) {
          if (Objects.requireNonNull(contents).potion().get().is(
            Identifier.fromNamespaceAndPath("minecraft", "strong_strength")
          ) && ingredient.is(Items.REDSTONE)) {
            cir.setReturnValue(true);
            return;
          } else if (Objects.requireNonNull(contents).potion().get().is(
            Identifier.fromNamespaceAndPath("minecraft", "long_strength")
          ) && ingredient.is(Items.GLOWSTONE_DUST)) {
            cir.setReturnValue(true);
            return;
          }
        }
      }
    }
  }
  
  @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
  private static void doBrew(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
    ItemStack ingredient = items.get(3);
    if (ingredient.isEmpty()) return;
    
    for (int i=0; i<3; i++) {
      ItemStack bottle = items.get(i);
      if (bottle.isEmpty()) continue;
      
      PotionContents contents = bottle.get(DataComponents.POTION_CONTENTS);
      if (contents == null || !contents.hasEffects()) continue;
      
      if (contents.potion().isPresent() && contents.potion().get().is(
        Identifier.fromNamespaceAndPath("minecraft", "strong_strength")
      )) {
        if (ingredient.is(Items.REDSTONE)) setFinalStrength(bottle, items, i);
        else items.set(i, level.potionBrewing().mix(ingredient, items.get(i)));
      } else if (contents.potion().isPresent() && contents.potion().get().is(
        Identifier.fromNamespaceAndPath("minecraft", "long_strength")
      )) {
        if (ingredient.is(Items.GLOWSTONE_DUST)) setFinalStrength(bottle, items, i);
        else items.set(i, level.potionBrewing().mix(ingredient, items.get(i)));
      } else if (!contents.customEffects().isEmpty() &&
        contents.customEffects().getFirst().compareTo(
        new MobEffectInstance(MobEffects.STRENGTH, 9600, 1)
      ) == 0) {
        if (bottle.is(Items.POTION) && ingredient.is(Items.GUNPOWDER))
          setFinalStrength(new ItemStack(Items.SPLASH_POTION), items, i);
        else if (bottle.is(Items.SPLASH_POTION) && ingredient.is(Items.DRAGON_BREATH))
          setFinalStrength(new ItemStack(Items.LINGERING_POTION), items, i);
        else items.set(i, level.potionBrewing().mix(ingredient, items.get(i)));
      } else items.set(i, level.potionBrewing().mix(ingredient, items.get(i)));
    }
    
    ingredient.shrink(1);
    ci.cancel();
  }
}
