package me.Thelnfamous1.blood_system.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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

import java.util.Optional;

public class BloodSystemCommands {
    public static final SimpleCommandExceptionType ERROR_MISSING_BLOOD_DATA = new SimpleCommandExceptionType(Component.translatable(BloodSystemMod.translationKey("commands", "missing")));
    public static final String GET_BLOOD_SUCCESS = BloodSystemMod.translationKey("commands", "blood.get.success");
    public static final String SET_BLOOD_SUCCESS = BloodSystemMod.translationKey("commands", "blood.set.success");
    public static final String GET_BLOOD_TYPE_SUCCESS = BloodSystemMod.translationKey("commands", "blood_type.get.success");
    public static final SimpleCommandExceptionType ERROR_NO_BLOOD_TYPE = new SimpleCommandExceptionType(Component.translatable(BloodSystemMod.translationKey("commands", "blood_type.get.none")));
    public static final String SET_BLOOD_TYPE_SUCCESS = BloodSystemMod.translationKey("commands", "blood_type.set.success");

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        registerCommands(event.getDispatcher());
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(BloodSystemMod.MODID)
                .requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("blood")
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            Player player = EntityArgument.getPlayer(ctx, "player");
                                            return BloodCapabilityProvider.getCapability(player).map(cap -> {
                                                ctx.getSource().sendSuccess(Component.translatable(GET_BLOOD_SUCCESS, player.getDisplayName(), cap.getBlood()), false);
                                                return Command.SINGLE_SUCCESS;
                                            }).orElseThrow(ERROR_MISSING_BLOOD_DATA::create);
                                        })))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("blood", FloatArgumentType.floatArg(0.0F))
                                                .executes(ctx -> {
                                                    Player player = EntityArgument.getPlayer(ctx, "player");
                                                    return BloodCapabilityProvider.getCapability(player).map(cap -> {
                                                        float blood = FloatArgumentType.getFloat(ctx, "blood");
                                                        cap.setBlood(blood);
                                                        ctx.getSource().sendSuccess(Component.translatable(SET_BLOOD_SUCCESS, player.getDisplayName(), blood), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    }).orElseThrow(ERROR_MISSING_BLOOD_DATA::create);
                                                })))))
                .then(Commands.literal("blood_type")
                        .then(Commands.literal("randomize")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            Player player = EntityArgument.getPlayer(ctx, "player");
                                            return BloodCapabilityProvider.getCapability(player).map(cap -> {
                                                BloodType randomBloodType = BloodType.getRandom(player.getRandom());
                                                cap.setBloodType(randomBloodType);
                                                ctx.getSource().sendSuccess(Component.translatable(SET_BLOOD_TYPE_SUCCESS, player.getDisplayName(), randomBloodType.getDisplayName()), true);
                                                return Command.SINGLE_SUCCESS;
                                            }).orElseThrow(ERROR_MISSING_BLOOD_DATA::create);
                                        })))
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            Player player = EntityArgument.getPlayer(ctx, "player");
                                            Optional<Integer> result = BloodCapabilityProvider.getCapability(player).map(cap -> {
                                                BloodType bloodType = cap.getBloodType();
                                                if (bloodType != null) {
                                                    ctx.getSource().sendSuccess(Component.translatable(GET_BLOOD_TYPE_SUCCESS, player.getDisplayName(), bloodType.getDisplayName()), false);
                                                    return Command.SINGLE_SUCCESS;
                                                } else {
                                                    return 0; // NO SUCCESS
                                                }
                                            });
                                            if(result.isPresent() && result.get() <= 0){
                                                throw ERROR_NO_BLOOD_TYPE.create();
                                            }
                                            return result.orElseThrow(ERROR_MISSING_BLOOD_DATA::create);
                                        })))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("blood_type", EnumArgument.enumArgument(BloodType.class))
                                                .executes(ctx -> {
                                                    Player player = EntityArgument.getPlayer(ctx, "player");
                                                    BloodType bloodType = ctx.getArgument("blood_type", BloodType.class);
                                                    return BloodCapabilityProvider.getCapability(player).map(cap -> {
                                                        cap.setBloodType(bloodType);
                                                        ctx.getSource().sendSuccess(Component.translatable(SET_BLOOD_TYPE_SUCCESS, player.getDisplayName(), bloodType.getDisplayName()), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    }).orElseThrow(ERROR_MISSING_BLOOD_DATA::create);
                                                }))))));
    }

}
