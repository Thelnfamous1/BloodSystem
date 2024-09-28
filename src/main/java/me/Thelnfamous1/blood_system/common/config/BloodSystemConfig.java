package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import commoble.databuddy.config.ConfigHelper;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.codec.UnboundedNavigableMapCodec;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;


public class BloodSystemConfig {
    public static class Server {
        public static final Codec<NavigableMap<Integer, List<MobEffectData>>> BLOOD_LOSS_EFFECTS_CODEC = new UnboundedNavigableMapCodec<>(Codec.INT, Codec.list(MobEffectData.CODEC));
        public final ForgeConfigSpec.IntValue bloodRegenMinFoodLevel;
        public final ForgeConfigSpec.DoubleValue bloodRegenFrequency;
        public final ForgeConfigSpec.DoubleValue bloodRegenAmount;
        public final ForgeConfigSpec.DoubleValue bloodLossWhenTakingDamage;
        public final ForgeConfigSpec.DoubleValue bleedChanceWhenTakingDamage;
        public final ForgeConfigSpec.DoubleValue bleedChanceWhenTakingDamageExtra;
        public final ConfigHelper.ConfigObject<NavigableMap<Integer, List<MobEffectData>>> bloodLossEffects;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                   .push("server");

            this.bloodRegenMinFoodLevel = builder
                    .comment("The minimum food level required for passive blood regeneration to begin.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodRegenMinFoodLevel"))
                    .worldRestart()
                    .defineInRange("bloodRegenMinFoodLevel", 17, 0, 20);

            this.bloodRegenFrequency = builder
                    .comment("The frequency, in seconds, that a passive blood regeneration tick will occur. A value of 0.05 or less will cause passive blood regeneration every tick.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodRegenFrequency"))
                    .worldRestart()
                    .defineInRange("bloodRegenFrequency", 5.0, 0, 60);

            this.bloodRegenAmount = builder
                    .comment("The amount of blood recovered during a passive blood regeneration tick.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodRegenAmount"))
                    .worldRestart()
                    .defineInRange("bloodRegenAmount", 1.0, 0, 100);

            this.bloodLossWhenTakingDamage = builder
                    .comment("The amount of blood lost when taking a half heart of damage or more.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodLossPerDamageTaken"))
                    .worldRestart()
                    .defineInRange("bloodLossPerDamageTaken", 2.0, 0, 100);

            this.bleedChanceWhenTakingDamage = builder
                    .comment("The chance to have the Bleed status effect applied when taking a full heart of damage or more.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bleedChanceWhenTakingDamage"))
                    .worldRestart()
                    .defineInRange("bleedChanceWhenTakingDamage", 10.0, 0, 100);

            this.bleedChanceWhenTakingDamageExtra = builder
                    .comment("The additional chance, per additional full heart of damage taken, to have the Bleed status effect applied.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bleedChanceWhenTakingDamageExtra"))
                    .worldRestart()
                    .defineInRange("bleedChanceWhenTakingDamageExtra", 5.0, 0, 100);

            this.bloodLossEffects = ConfigHelper.defineObject(
                    builder.comment("The status effects to apply when a player's blood level is at or below these percentages.")
                            .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodLossEffects"))
                            .worldRestart(),
                    "bloodLossEffects", BLOOD_LOSS_EFFECTS_CODEC, new TreeMap<>(){
                        {
                            this.put(50, List.of(new MobEffectData(MobEffects.WEAKNESS, 0)));
                            this.put(30, List.of(new MobEffectData(MobEffects.WEAKNESS, 1), new MobEffectData(MobEffects.DIG_SLOWDOWN, 0)));
                            this.put(15, List.of(new MobEffectData(MobEffects.MOVEMENT_SLOWDOWN, 0)));
                            this.put(10, List.of(new MobEffectData(MobEffects.DARKNESS, 0)));
                        }
                    });

            builder.pop();
        }
    }

    /**
     * General configuration that doesn't need to be synchronized but needs to be available before server startup
     */
    public static class Common {

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General configuration settings")
                    .push("general");


            builder.pop();
        }
    }

    /**
     * Client specific configuration - only loaded clientside from blood_system-client.toml
     */
    public static class Client {
        public final ConfigHelper.ConfigObject<RenderCorner> bloodMeterRenderCorner;
        public final ForgeConfigSpec.IntValue bloodMeterXOffset;
        public final ForgeConfigSpec.IntValue bloodMeterYOffset;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                   .push("client");

            this.bloodMeterRenderCorner = ConfigHelper.defineObject(
                    builder
                            .comment("The corner of the screen to offset the top-left corner of the blood meter from.")
                            .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodMeterRenderCorner")),
                    "bloodMeterRenderCorner", RenderCorner.CODEC, RenderCorner.BOTTOM_RIGHT);

            this.bloodMeterXOffset = builder
                .comment("The amount of pixels to offset the x-coordinate of the top-left corner of the blood meter from the configured bloodMeterRenderCorner value.")
                .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodMeterXOffset"))
                .defineInRange("bloodMeterXOffset", 33, 0, Integer.MAX_VALUE);

            this.bloodMeterYOffset = builder
                    .comment("The amount of pixels to offset the y-coordinate of the top-left corner of the blood meter from the configured bloodMeterRenderCorner value.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodMeterYOffset"))
                    .defineInRange("bloodMeterYOffset", 33, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }


    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }


    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        if(configEvent.getConfig().getSpec() == serverSpec && serverSpec.isLoaded()){
            if(DebugFlags.DEBUG_BLOOD_LOSS_EFFECTS)
                BloodSystemMod.LOGGER.info("Loaded bloodLossEffects map: {}", getLegibleBloodLossEffects());
        }
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        if(configEvent.getConfig().getSpec() == serverSpec && serverSpec.isLoaded()){
            if(DebugFlags.DEBUG_BLOOD_LOSS_EFFECTS)
                BloodSystemMod.LOGGER.info("Reloaded bloodLossEffects map: {}", getLegibleBloodLossEffects());
        }
    }

    public static Map<Integer, List<Pair<ResourceLocation, Integer>>> getLegibleBloodLossEffects() {
        return SERVER.bloodLossEffects.get().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .map(MobEffectData::asPair)
                        .collect(Collectors.toList())
        ));
    }
}