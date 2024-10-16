package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import commoble.databuddy.config.ConfigHelper;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.codec.UnboundedNavigableMapCodec;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;


public class BloodSystemConfig {
    public static class Server {
        public static final Codec<NavigableMap<Integer, List<MobEffectData>>> BLOOD_LOSS_EFFECTS_CODEC = new UnboundedNavigableMapCodec<>(Codec.INT, Codec.list(MobEffectData.CODEC));
        public static final Codec<Map<ResourceLocation, Integer>> BATTERY_CHARGES_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT);
        public static final Codec<Map<ResourceLocation, ConsumeType>> BANDAGES_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, ConsumeType.CODEC);
        public final ForgeConfigSpec.IntValue bloodRegenMinFoodLevel;
        public final ForgeConfigSpec.DoubleValue bloodRegenFrequency;
        public final ForgeConfigSpec.DoubleValue bloodRegenAmount;
        public final ForgeConfigSpec.DoubleValue bloodLossWhenTakingDamage;
        public final ForgeConfigSpec.DoubleValue bleedChanceWhenTakingDamage;
        public final ForgeConfigSpec.DoubleValue bleedChanceWhenTakingDamageExtra;
        public final ForgeConfigSpec.DoubleValue bleedFrequency;
        public final ForgeConfigSpec.DoubleValue bleedAmount;
        public final ConfigHelper.ConfigObject<NavigableMap<Integer, List<MobEffectData>>> bloodLossEffects;
        public final ConfigHelper.ConfigObject<Map<ResourceLocation, Integer>> batteryCharges;
        private final Map<Item, Integer> parsedBatteryCharges = new HashMap<>();
        public final ConfigHelper.ConfigObject<Map<ResourceLocation, ConsumeType>> bandages;
        private final Map<Item, ConsumeType> parsedBandages = new HashMap<>();
        public final ForgeConfigSpec.DoubleValue bloodRegenFoodExhaustion;
        public final ForgeConfigSpec.DoubleValue bloodAnalyzerAnalysisTime;
        public final ForgeConfigSpec.DoubleValue microscopeAnalysisTime;

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

            this.bloodRegenFoodExhaustion = builder
                    .comment("The amount of food exhaustion caused by a passive blood regeneration tick. In vanilla, accumulating 4 food exhaustion will decrease saturation by 1, or food level by 1 if saturation is 0.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodRegenFoodExhaustion"))
                    .worldRestart()
                    .defineInRange("bloodRegenFoodExhaustion", 6.0, 0, 100);

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
                    .comment("The additional chance, per additional full heart of damage taken, to have the Bleeding status effect applied.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bleedChanceWhenTakingDamageExtra"))
                    .worldRestart()
                    .defineInRange("bleedChanceWhenTakingDamageExtra", 5.0, 0, 100);

            this.bleedFrequency = builder
                    .comment("The frequency, in seconds, that a Bleeding status effect tick will occur. A value of 0.05 or less will cause blood loss every tick.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bleedFrequency"))
                    .worldRestart()
                    .defineInRange("bleedFrequency", 30.0, 0, Double.MAX_VALUE);

            this.bleedAmount = builder
                    .comment("The amount of blood lost during a Bleeding status effect tick.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bleedAmount"))
                    .worldRestart()
                    .defineInRange("bleedAmount", 1.0, 0, Double.MAX_VALUE);

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

            this.batteryCharges = ConfigHelper.defineObject(
                    builder.comment("The maximum charges each battery provides to a Blood Analyzer or Microscope when placed in a battery slot.")
                            .translation(BloodSystemMod.translationKeySuffixed("configgui.batteryCharges"))
                            .worldRestart(),
                    "batteryCharges", BATTERY_CHARGES_CODEC, new HashMap<>(){
                        {
                            this.put(new ResourceLocation("zombie_extreme:batteries"), 1);
                            this.put(new ResourceLocation("apocalypsenow:aabattery"), 2);
                            this.put(new ResourceLocation("zombie_extreme:energy_battery"), 4);
                        }
                    });

            this.bandages = ConfigHelper.defineObject(
                    builder.comment("The bandages that remove an active Bleeding status effect when consumed.")
                            .translation(BloodSystemMod.translationKeySuffixed("configgui.bandages"))
                            .worldRestart(),
                    "bandages", BANDAGES_CODEC, new HashMap<>(){
                        {
                            this.put(new ResourceLocation("zombie_extreme:bandage"), ConsumeType.FINISH);
                            this.put(new ResourceLocation("apocalypsenow:bandage"), ConsumeType.RIGHT_CLICK);
                        }
                    });

            this.bloodAnalyzerAnalysisTime = builder
                    .comment("The amount of time, in seconds, that it takes for the Blood Analyzer to analyse a blood container.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.bloodAnalyzerAnalysisTime"))
                    .worldRestart()
                    .defineInRange("bloodAnalyzerAnalysisTime", 120.0, 0, Double.MAX_VALUE);

            this.microscopeAnalysisTime = builder
                    .comment("The amount of time, in seconds, that it takes for the Microscope to analyse a blood container.")
                    .translation(BloodSystemMod.translationKeySuffixed("configgui.microscopeAnalysisTime"))
                    .worldRestart()
                    .defineInRange("microscopeAnalysisTime", 120.0, 0, Double.MAX_VALUE);

            builder.pop();
        }

        public boolean isBattery(Item item){
            return this.parsedBatteryCharges.containsKey(item);
        }

        public int getBatteryCharge(Item item){
            return this.parsedBatteryCharges.getOrDefault(item, 0);
        }

        public boolean isBandage(Item item) {
            return this.parsedBandages.containsKey(item);
        }

        @Nullable
        public ConsumeType getBandageConsumeType(Item item){
            return this.parsedBandages.get(item);
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
            parseBatteryCharges(false);
            parseBandages(false);
        }
    }

    private static void parseBatteryCharges(boolean reload) {
        SERVER.parsedBatteryCharges.clear();
        SERVER.batteryCharges.get().forEach((key, value) -> {
            Optional<Item> parsedItem = Registry.ITEM.getOptional(key);
            parsedItem.ifPresentOrElse(
                    item -> SERVER.parsedBatteryCharges.put(item, value),
                    () -> BloodSystemMod.LOGGER.error("Invalid item in batteryCharges: {}", key));
        });
        if(DebugFlags.DEBUG_BATTERY_CHARGES)
            BloodSystemMod.LOGGER.info("{} batteryCharges map: {}", reload ? "Reloaded" : "Loaded", SERVER.batteryCharges.get());
    }

    private static void parseBandages(boolean reload) {
        SERVER.parsedBandages.clear();
        SERVER.bandages.get().forEach((key, value) -> {
            Optional<Item> parsedItem = Registry.ITEM.getOptional(key);
            parsedItem.ifPresentOrElse(
                    item -> SERVER.parsedBandages.put(item, value),
                    () -> BloodSystemMod.LOGGER.error("Invalid item in bandages: {}", key));
        });
        if(DebugFlags.DEBUG_BANDAGES)
            BloodSystemMod.LOGGER.info("{} bandages map: {}", reload ? "Reloaded" : "Loaded", SERVER.bandages.get());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        if(configEvent.getConfig().getSpec() == serverSpec && serverSpec.isLoaded()){
            if(DebugFlags.DEBUG_BLOOD_LOSS_EFFECTS)
                BloodSystemMod.LOGGER.info("Reloaded bloodLossEffects map: {}", getLegibleBloodLossEffects());
            parseBatteryCharges(true);
            parseBandages(true);
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