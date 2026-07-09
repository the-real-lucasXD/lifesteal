package sgnl.lifesteal.heart.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
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
public class HeartCommand {
  @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void register(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    try {
      this.dispatcher.register(literal("hearts")
        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
        .then(argument("player", EntityArgument.player())
          .then(literal("add").then(argument("amount", IntegerArgumentType.integer(1))
            .executes(ctx -> {
              try {
                Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
                player.giveHealth(IntegerArgumentType.getInteger(ctx, "amount"), false);
                return 0;
              } catch (Exception e) {
                Lifesteal.LOGGER.error("An error occurred while executing a command:", e);
                ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
                return 0;
              }
            })
          )).then(literal("remove").then(argument("amount", IntegerArgumentType.integer(1))
            .executes(ctx -> {
              try {
                Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
                player.removeHealth(IntegerArgumentType.getInteger(ctx, "amount"));
                return 0;
              } catch (Exception e) {
                Lifesteal.LOGGER.error("An error occurred while executing a command:", e);
                ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
                return 0;
              }
            })
          )).then(literal("get").executes(ctx -> {
            try {
              Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
              ctx.getSource().sendSuccess(() -> {
                try {
                  return Component.literal(String.format(
                    "%s has %d health.",
                    EntityArgument.getPlayer(ctx, "player").getScoreboardName(),
                    player.health()
                  ));
                } catch (Exception e) {
                  return Component.literal("An error occurred while executing this command.")
                    .withColor(TextColor.RED);
                }
              }, false);
              return 0;
            } catch (Exception e) {
              Lifesteal.LOGGER.error("An error occurred while executing a command:", e);
              ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
              return 0;
            }
          })).then(literal("set").then(argument("amount", IntegerArgumentType.integer(1))
            .executes(ctx -> {
              try {
                int hearts = IntegerArgumentType.getInteger(ctx, "amount");
                if (hearts < Config.minHealth() || hearts > Config.maxHealth()) {
                  ctx.getSource().sendFailure(Component.literal("Heart amount out of bounds!"));
                  return 0;
                }
                Player player = Config.getPlayer(EntityArgument.getPlayer(ctx, "player"));
                player.setHealth(hearts);
                return 0;
              } catch (Exception e) {
                Lifesteal.LOGGER.error("An error occurred while executing a command:", e);
                ctx.getSource().sendFailure(Component.literal("An error occurred while executing this command."));
                return 0;
              }
            })
          ))
        )
      );
    } catch (Exception e) {
      Lifesteal.LOGGER.error("Error in HeartCommand.java/register", e);
    }
  }
}
