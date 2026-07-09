package sgnl.lifesteal.combat.mixins.bans;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

import java.util.Objects;

@Mixin(Commands.class)
public class ExecutingCommands {
  @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
  private void performCommand(ParseResults<CommandSourceStack> command, String commandString, CallbackInfo ci) {
    try {
      if (!commandString.startsWith("combat ")) {
        Player player = Config.getPlayer(Objects.requireNonNull(command.getContext().getSource().getPlayer()));
        if (!player.inCombat() || player.serverPlayer().isCreative()) return;
        command.getContext().getSource().sendSystemMessage(
          Component.literal("You cannot use this command while in combat!").withColor(TextColor.RED)
        );
        ci.cancel();
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in ExecutingCommands.java/performCommand", e);
    }
  }
}