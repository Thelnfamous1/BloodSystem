package me.Thelnfamous1.blood_system;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
import me.Thelnfamous1.blood_system.common.datagen.BloodSystemDatagen;
import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(BloodSystemMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BloodSystemMod {
    public static final String MODID = "blood_system";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

    public static final RegistryObject<Attribute> MAX_BLOOD = ATTRIBUTES.register("blood", () -> new RangedAttribute(translationKey("attribute", "max_blood"), 100.0D, 1.0D, Double.MAX_VALUE).setSyncable(true));

    public BloodSystemMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BloodSystemConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BloodSystemConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BloodSystemConfig.commonSpec);
        modEventBus.register(BloodSystemConfig.class);
        modEventBus.register(BloodSystemDatagen.class);
        ATTRIBUTES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(BloodCapabilityProvider.class);
        MinecraftForge.EVENT_BUS.register(BloodSystemCommands.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Do something when the setup is run on both client and server
        event.enqueueWork(BloodSystemNetwork::init);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Do something when the setup is run on only the client
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event){
        event.add(EntityType.PLAYER, MAX_BLOOD.get());
    }

    public static ResourceLocation location(String path){
        return new ResourceLocation(MODID, path);
    }

    public static String translationKey(String path){
        return MODID + "." + path;
    }

    public static String translationKey(String type, String path){
        return type + "." + MODID + "." + path;
    }
}
