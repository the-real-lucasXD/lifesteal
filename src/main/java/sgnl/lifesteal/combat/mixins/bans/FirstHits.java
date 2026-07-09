package sgnl.lifesteal.combat.mixins.bans;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

@Mixin(net.minecraft.world.entity.player.Player.class)
public class FirstHits {
  
  @Inject(method = "attack", at = @At("HEAD"))
  private void attack(Entity entity, CallbackInfo ci) {
    try {
      if (entity instanceof ServerPlayer) {
        if ((Object) this instanceof ServerPlayer serverPlayer) {
          Player player = Config.getPlayer(serverPlayer);
          if (serverPlayer.getMainHandItem().is(Items.MACE) && serverPlayer.fallDistance > 1.5F && !player.inCombat()) {
            serverPlayer.fallDistance = 1.1F;
            serverPlayer.sendSystemMessage(
              Component.literal("You cannot do a mace smash as your first attack!").withColor(TextColor.RED),
              true
            );
            player.cancelAttack = true;
          }
        }
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in FirstHits.java/attack", e);
    }
  }

  @ModifyVariable(
    method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
    at = @At("HEAD"), argsOnly = true, name = "damage")
  private float modifyHurtDamage(float damage, ServerLevel level, DamageSource source) {
    try {
      if ((Object) this instanceof ServerPlayer) {
        if (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.SPEAR)) {
          ServerPlayer serverPlayer = (ServerPlayer) source.getEntity();
          Player player = Config.getPlayer(serverPlayer);
          if (player.firstCombatHit) {
            if (player.cancelAttack) {
              player.cancelAttack = false;
              return 1.0F;
            } else if (
              serverPlayer.getMainHandItem().is(Items.STONE_SPEAR) ||
                serverPlayer.getMainHandItem().is(Items.IRON_SPEAR) ||
                serverPlayer.getMainHandItem().is(Items.COPPER_SPEAR) ||
                serverPlayer.getMainHandItem().is(Items.GOLDEN_SPEAR) ||
                serverPlayer.getMainHandItem().is(Items.DIAMOND_SPEAR) ||
                serverPlayer.getMainHandItem().is(Items.NETHERITE_SPEAR)
            ) {
              serverPlayer.sendSystemMessage(
                Component.literal("You cannot spear as a first hit!").withColor(TextColor.RED),
                true
              );
              return 1.0F;
            }
          }
        }
      }
      return damage;
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in FirstHits.java/modifyHurtDamage", e);
      return damage;
    }
  }
}