package me.Thelnfamous1.blood_system.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.EnumArgument;

public class BloodSystemCommands {

    public static final String GET_BLOOD_TYPE_SUCCESS = BloodSystemMod.translationKey("commands", "blood_type.get.success");
    public static final String SET_BLOOD_TYPE_SUCCESS = BloodSystemMod.translationKey("commands", "blood_type.set.success");

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        registerCommands(event.getDispatcher());
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(BloodSystemMod.MODID)
                .requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("blood_type")
                        .then(Commands.literal("randomize")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            Player player = EntityArgument.getPlayer(ctx, "player");
                                            BloodCapabilityProvider.getCapability(player).ifPresent(cap -> {
                                                BloodType randomBloodType = BloodType.getRandom(player.getRandom());
                                                cap.setBloodType(randomBloodType);
                                                ctx.getSource().sendSuccess(Component.translatable(SET_BLOOD_TYPE_SUCCESS, player.getDisplayName(), randomBloodType.getDisplayName()), true);
                                            });
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            Player player = EntityArgument.getPlayer(ctx, "player");
                                            BloodCapabilityProvider.getCapability(player).ifPresent(cap -> {
                                                BloodType bloodType = cap.getBloodType();
                                                ctx.getSource().sendSuccess(Component.translatable(GET_BLOOD_TYPE_SUCCESS, player.getDisplayName(), bloodType), false);
                                            });
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("blood_type", EnumArgument.enumArgument(BloodType.class))
                                                .executes(ctx -> {
                                                    Player player = EntityArgument.getPlayer(ctx, "player");
                                                    BloodType bloodType = ctx.getArgument("blood_type", BloodType.class);
                                                    BloodCapabilityProvider.getCapability(player).ifPresent(cap -> {
                                                        cap.setBloodType(bloodType);
                                                        ctx.getSource().sendSuccess(Component.translatable(SET_BLOOD_TYPE_SUCCESS, player.getDisplayName(), bloodType.getDisplayName()), true);
                                                    });
                                                    return Command.SINGLE_SUCCESS;
                                                }))))));
    }

}
