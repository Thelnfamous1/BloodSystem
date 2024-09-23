package me.Thelnfamous1.blood_system;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.blood_system.config.BloodSystemConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BloodSystemMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BloodSystemMod {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BloodSystemMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BloodSystemConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BloodSystemConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BloodSystemConfig.commonSpec);
        modEventBus.register(BloodSystemConfig.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Do something when the setup is run on both client and server
        LOGGER.info("HELLO from common setup!");
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Do something when the setup is run on only the client
        LOGGER.info("HELLO from client setup!");
    }

    public static ResourceLocation location(String path){
        return new ResourceLocation(MODID, path);
    }

    public static String translationKey(String path){
        return MODID + "." + path;
    }
}
