package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.player.Player;

@Mixin(ServerPlayer.class)
public class MaceNerf {
  @ModifyVariable(
    method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
    at = @At("HEAD"), argsOnly = true, name = "damage")
  private float modifyHurtDamage(float damage, ServerLevel level, DamageSource source) {
    if (source.is(DamageTypes.MACE_SMASH)) {
      if (source.getEntity() instanceof ServerPlayer) {
        Player player = Config.getPlayer((ServerPlayer) (Object) this);
        return Math.min(damage, player.health * 1.2f);
      }
    } return damage;
  }
}
