package sgnl.lifesteal.heart.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class WithdrawCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    try {
      this.dispatcher.register(
        literal("withdraw").then(argument("amount", IntegerArgumentType.integer(1))
          .executes(ctx -> {
            try {
              int amount = IntegerArgumentType.getInteger(ctx, "amount");
              Player player = Config.getPlayer(Objects.requireNonNull(ctx.getSource().getPlayer()));
              int withdrawn = amount - player.removeHealth(amount);
              if (withdrawn <= 0) ctx.getSource().sendFailure(Component.literal("No hearts were removed."));
              else {
                player.giveHeartObject(withdrawn);
                ctx.getSource().sendSuccess(() -> Component.literal(
                  String.format("Successfully withdrawn %d hearts.", withdrawn)
                ), false);
              }
              return 0;
            } catch (Exception e) {
              Lifesteal.LOGGER.error("Error while executing command /withdraw <amount>", e);
              ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
              return 0;
            }
          })
        )
      );
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in WithdrawCommand.java/init", e);
    }
  }
}
