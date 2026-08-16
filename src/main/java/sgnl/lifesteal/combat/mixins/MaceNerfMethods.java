package sgnl.lifesteal.combat.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface MaceNerfMethods {
  @Invoker("getDamageAfterArmorAbsorb")
  float damageAfterArmorAbsorb(final DamageSource damageSource, float damage);

  @Invoker("getDamageAfterMagicAbsorb")
  float damageAfterMagicAbsorb(final DamageSource damageSource, float damage);
}
