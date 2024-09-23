package me.Thelnfamous1.blood_system.config;

import com.mojang.serialization.Codec;
import commoble.databuddy.config.ConfigHelper;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.MobEffectData;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

import java.util.Map;


public class BloodSystemConfig {
    public static class Server {
        public static final Codec<Map<Double, MobEffectData>> BLOOD_LOSS_EFFECTS_CODEC = Codec.unboundedMap(Codec.DOUBLE, MobEffectData.CODEC);
        public final ForgeConfigSpec.DoubleValue bloodRegenHunger;
        public final ForgeConfigSpec.DoubleValue bloodRegenFrequency;
        public final ForgeConfigSpec.DoubleValue bloodRegenAmount;
        public final ForgeConfigSpec.DoubleValue bloodLossWhenTakingDamage;
        public final ForgeConfigSpec.DoubleValue bleedChanceWhenTakingDamage;
        public final ConfigHelper.ConfigObject<Map<Double, MobEffectData>> bloodLossEffects;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                   .push("server");

            bloodRegenHunger = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodRegenHunger"))
                    .worldRestart()
                    .defineInRange("bloodRegenHunger", 0.5, 0, 10);

            bloodRegenFrequency = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodRegenFrequency"))
                    .worldRestart()
                    .defineInRange("bloodRegenFrequency", 0.5, 0, 10);

            bloodRegenAmount = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodRegenAmount"))
                    .worldRestart()
                    .defineInRange("bloodRegenAmount", 0.5, 0, 10);

            bloodLossWhenTakingDamage = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodLossPerDamageTaken"))
                    .worldRestart()
                    .defineInRange("bloodLossPerDamageTaken", 0.5, 0, 10);

            bleedChanceWhenTakingDamage = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodLossPerDamageTaken"))
                    .worldRestart()
                    .defineInRange("bloodLossPerDamageTaken", 0.5, 0, 10);

            bloodLossEffects = ConfigHelper.defineObject(builder, "bloodLossEffect", BLOOD_LOSS_EFFECTS_CODEC, Map.of());

            builder.pop();
        }
    }

    /**
     * General configuration that doesn't need to be synchronized but needs to be available before server startup
     */
    public static class Common {
        public final BooleanValue cachePackAccess;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("[DEPRECATED / NO EFFECT]: General configuration settings")
                    .push("general");

            cachePackAccess = builder
                    .comment("[DEPRECATED / NO EFFECT] [NOW IN RESOURCE-CACHING CONFIG]: Set this to true to cache resource listings in resource and data packs")
                    .translation("forge.configgui.cachePackAccess")
                    .worldRestart()
                    .define("cachePackAccess", true);


            builder.pop();
        }
    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {
        public final BooleanValue alwaysSetupTerrainOffThread;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                   .push("client");

            alwaysSetupTerrainOffThread = builder
                .comment("Enable Forge to queue all chunk updates to the Chunk Update thread.",
                        "May increase FPS significantly, but may also cause weird rendering lag.",
                        "Not recommended for computers without a significant number of cores available.")
                .translation("forge.configgui.alwaysSetupTerrainOffThread")
                .define("alwaysSetupTerrainOffThread", false);

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
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
    }
}