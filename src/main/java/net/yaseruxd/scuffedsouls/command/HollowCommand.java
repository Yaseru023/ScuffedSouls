package net.yaseruxd.scuffedsouls.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yaseruxd.scuffedsouls.hollow.HollowData;
import net.yaseruxd.scuffedsouls.hollow.HollowManager;

@Mod.EventBusSubscriber(modid = "scuffedsouls", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("hollow")
                        .requires(src -> src.hasPermission(2))

                        // /hollow get [player]
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            int level = HollowData.getLevel(target);
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            target.getName().getString()
                                                                    + " hollow level: " + level + "/5"),
                                                    false);
                                            return level;
                                        })))

                        // /hollow set <player> <level>
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 5))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    int level = IntegerArgumentType.getInteger(ctx, "level");

                                                    HollowData.setLevel(target, level);
                                                    HollowManager.removeModifiers(target);
                                                    HollowManager.applyModifiers(target);

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal(
                                                                    "Set " + target.getName().getString()
                                                                            + " hollow level to " + level + "/5"),
                                                            true);
                                                    return level;
                                                }))))

                        // /hollow reset <player>
                        .then(Commands.literal("reset")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");

                                            HollowData.setLevel(target, 0);
                                            HollowManager.removeModifiers(target);

                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "Reset " + target.getName().getString()
                                                                    + "'s hollow level"),
                                                    true);
                                            return 0;
                                        })))
        );
    }
}