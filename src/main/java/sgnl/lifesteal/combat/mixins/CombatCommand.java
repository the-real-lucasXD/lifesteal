package sgnl.lifesteal.combat.mixins;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sgnl.lifesteal.Config;
import sgnl.lifesteal.Lifesteal;
import sgnl.lifesteal.player.Player;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class CombatCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    try {
      this.dispatcher.register(literal("combat")
        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
        .then(argument("player", EntityArgument.player())
          .then(literal("clear")
            .executes(ctx -> {
              try {
                Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
                player.combat.clear();
                ctx.getSource().sendSuccess(() -> Component.literal(
                  "Successfully cleared combat of selected player."
                ), true);
              } catch (Exception e) {
                Lifesteal.LOGGER.error(e.getMessage());
                ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
              }
              return 0;
            })
          ).then(literal("enter")
            .then(argument("opponent", EntityArgument.player())
              .executes(ctx -> {
                try {
                  Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
                  Player opponent = Config.getPlayer(EntityArgument.getPlayer(ctx, "opponent"));
                  player.enterCombat(opponent);
                  opponent.enterCombat(player);
                  ctx.getSource().sendSuccess(() -> Component.literal(
                    "Player has successfully entered combat."
                  ), true);
                } catch (Exception e) {
                  Lifesteal.LOGGER.error(e.getMessage());
                  ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
                }
                return 0;
              })
            ).executes(ctx -> {
              try {
                Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
                player.enterCombat(null);
                ctx.getSource().sendSuccess(() -> Component.literal(
                  "Player has successfully entered combat."
                ), true);
              } catch (Exception e) {
                Lifesteal.LOGGER.error(e.getMessage());
                ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
              }
              return 0;
            })
          )
        )
      );
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in CombatCommand.java/init", e);
    }
  }
}