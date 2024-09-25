package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import commoble.databuddy.config.ConfigHelper;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.codec.UnboundedNavigableMapCodec;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


public class BloodSystemConfig {
    public static class Server {
        public static final Codec<NavigableMap<Float, List<MobEffectData>>> BLOOD_LOSS_EFFECTS_CODEC = new UnboundedNavigableMapCodec<>(Codec.FLOAT, Codec.list(MobEffectData.CODEC));
        public final ForgeConfigSpec.IntValue bloodRegenFoodLevel;
        public final ForgeConfigSpec.DoubleValue bloodRegenFrequency;
        public final ForgeConfigSpec.DoubleValue bloodRegenAmount;
        public final ForgeConfigSpec.DoubleValue bloodLossWhenTakingDamage;
        public final ForgeConfigSpec.DoubleValue bleedChanceWhenTakingDamage;
        public final ConfigHelper.ConfigObject<NavigableMap<Float, List<MobEffectData>>> bloodLossEffects;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                   .push("server");

            this.bloodRegenFoodLevel = builder
                    .comment("The food level required for passive blood regeneration to begin.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodRegenFoodLevel"))
                    .worldRestart()
                    .defineInRange("bloodRegenFoodLevel", 10, 0, 20);

            this.bloodRegenFrequency = builder
                    .comment("The frequency, in seconds, that a passive blood regeneration tick will occur.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodRegenFrequency"))
                    .worldRestart()
                    .defineInRange("bloodRegenFrequency", 0.5, 0, 60);

            this.bloodRegenAmount = builder
                    .comment("The amount of blood recovered during a passive blood regeneration tick.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodRegenAmount"))
                    .worldRestart()
                    .defineInRange("bloodRegenAmount", 1.0D, 0, 100);

            this.bloodLossWhenTakingDamage = builder
                    .comment("The amount of blood lost when taking a half heart of damage or more.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodLossPerDamageTaken"))
                    .worldRestart()
                    .defineInRange("bloodLossPerDamageTaken", 0.5, 0, 10);

            this.bleedChanceWhenTakingDamage = builder
                    .comment("The chance to have the Bleed status effect applied when taking a full heart of damage or more.")
                    .translation(BloodSystemMod.translationKey("configgui.bleedChanceWhenTakingDamage"))
                    .worldRestart()
                    .defineInRange("bleedChanceWhenTakingDamage", 0.5, 0, 10);

            this.bloodLossEffects = ConfigHelper.defineObject(
                    builder.comment("The status effects to apply when a player's blood level is at or below these percentages.")
                            .translation(BloodSystemMod.translationKey("configgui.bloodLossEffects"))
                            .worldRestart(),
                    "bloodLossEffects", BLOOD_LOSS_EFFECTS_CODEC, Collections.emptyNavigableMap());

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
                            .translation(BloodSystemMod.translationKey("configgui.bloodMeterRenderCorner")),
                    "bloodMeterRenderCorner", RenderCorner.CODEC, RenderCorner.BOTTOM_RIGHT);

            this.bloodMeterXOffset = builder
                .comment("The amount of pixels to offset the x-coordinate of the top-left corner of the blood meter from the configured bloodMeterRenderCorner value.")
                .translation(BloodSystemMod.translationKey("configgui.bloodMeterXOffset"))
                .defineInRange("bloodMeterXOffset", 32, 0, Integer.MAX_VALUE);

            this.bloodMeterYOffset = builder
                    .comment("The amount of pixels to offset the y-coordinate of the top-left corner of the blood meter from the configured bloodMeterRenderCorner value.")
                    .translation(BloodSystemMod.translationKey("configgui.bloodMeterYOffset"))
                    .defineInRange("bloodMeterYOffset", 32, 0, Integer.MAX_VALUE);

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