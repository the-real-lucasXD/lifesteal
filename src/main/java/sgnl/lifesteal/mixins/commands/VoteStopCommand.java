package sgnl.lifesteal.mixins.commands;

import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.dialog.Dialog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.VoteStop;

import java.util.Objects;

import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class VoteStopCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    try {
      this.dispatcher.register(literal("votestop")
        .executes(ctx -> {
          try {
            String dialogJSON = Config.getVariable("votestop");
            if (dialogJSON == null) return 0;
            String result;
            if (VoteStop.requirement <= VoteStop.voted.size() + 1) result = String.format(
              "the server will shut down as the required number of votes (%d) is reached",
              VoteStop.requirement
            );
            else result = String.format(
              "%d more players need to vote to shut down the server.",
              VoteStop.requirement - VoteStop.voted.size() - 1
            );
            dialogJSON = dialogJSON.replaceAll("<result>", result);
            Objects.requireNonNull(ctx.getSource().getPlayer()).openDialog(
              Dialog.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(dialogJSON)).getPartialOrThrow()
            );
            return 0;
          } catch (Exception e) {
            Lifesteal.LOGGER.error("Error occurred in /votestop", e);
            ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
            return 0;
          }
        })
      );
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error occurred in VoteStopCommand.java/init", e);
    }
  }
}