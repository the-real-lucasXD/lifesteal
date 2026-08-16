package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.combat.mixins.MaceNerfMethods;


@Mixin(Player.class)
public abstract class MaceNerf {
  @Shadow public abstract void awardStat(Identifier location, int count);

  @Shadow public abstract void causeFoodExhaustion(float amount);

  @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
  private void actuallyHurt(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
    if (!source.is(DamageTypes.MACE_SMASH)) return;

    LivingEntity entity = (LivingEntity) (Object) this;
    if (!(entity instanceof ServerPlayer)) return;

    dmg = ((MaceNerfMethods) this).damageAfterArmorAbsorb(source, dmg);
    dmg = ((MaceNerfMethods) this).damageAfterMagicAbsorb(source, dmg);

    dmg = (float) Math.min(dmg, Math.floor(Config.getPlayer((ServerPlayer) entity).health * 1.2));

    float originalDamage = dmg;

    dmg = Math.max(dmg - entity.getAbsorptionAmount(), 0.0F);
    entity.setAbsorptionAmount(entity.getAbsorptionAmount() - (originalDamage - dmg));

    float absorbedDamage = originalDamage - dmg;
    if (absorbedDamage > 0.0F) {
      this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(absorbedDamage * 10.0F));
    }

    if (dmg != 0.0F) {
      this.causeFoodExhaustion(source.getFoodExhaustion());
      entity.getCombatTracker().recordDamage(source, dmg);
      entity.setHealth(entity.getHealth() - dmg);
      this.awardStat(Stats.DAMAGE_TAKEN, Math.round(dmg * 10.0F));
      entity.gameEvent(GameEvent.ENTITY_DAMAGE);
    }

    ci.cancel();
  }
}