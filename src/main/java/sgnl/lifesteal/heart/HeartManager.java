package sgnl.lifesteal.heart;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import sgnl.lifesteal.Config;

import java.util.Objects;
import java.util.UUID;

public interface HeartManager {
  int health();
  void setHealth(int amount);
  UUID uuid();
  ServerPlayer serverPlayer();
  boolean inCombat();
  
  default void updateHealth(int amount) { setHealth(health() + amount); }
  
  default void updateHealth() {
    Objects.requireNonNull(serverPlayer().getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health()*2);
  }
  
  default int giveHealth(int amount, boolean overflow) {
    if (amount <= 0) return amount;
    updateHealth(amount);
    int overflowAmount = Math.max(0, health() - Config.maxHealth());
    if (overflowAmount > 0) {
      setHealth(Config.maxHealth());
      if (overflow) giveHeartObject(overflowAmount);
      else return overflowAmount;
    } return 0;
  }
  
  default ItemStack createHeart(int amount) {
    ItemStack heart = new ItemStack(Items.NETHER_STAR, amount);
    CompoundTag data = new CompoundTag();
    data.putByte("lifesteal_heart", (byte) 1);
    data.putByte("lifesteal_custom_object", (byte) 1);
    heart.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    heart.set(DataComponents.ITEM_NAME, Component.literal("§r§f§dHeart"));
    return heart;
  }
  
  default void giveHeartObject(int amount) {
    if (amount <= 0) return;
    ItemStack heart = createHeart(amount);
    serverPlayer().getInventory().add(heart);
    if (!heart.isEmpty()) serverPlayer().drop(heart, false);
  }
  
  default int removeHealth(int amount) {
    if (amount <= 0) return amount;
    updateHealth(-amount);
    int overflowAmount = Math.max(0, Config.minHealth() - health());
    if (overflowAmount > 0) setHealth(Config.minHealth());
    return overflowAmount;
  }
  
  default void dropHeart() {
    ItemStack heart = createHeart(1);
    ItemEntity itemEntity = new ItemEntity(
      serverPlayer().level(),
      serverPlayer().getX(),
      serverPlayer().getY(),
      serverPlayer().getZ(),
      heart
    );
    
    itemEntity.setDeltaMovement(
      (serverPlayer().getRandom().nextFloat() - 0.5) * 0.1,
      0.1,
      (serverPlayer().getRandom().nextFloat() - 0.5) * 0.1
    );
    
    serverPlayer().level().addFreshEntity(itemEntity);
  }
}
