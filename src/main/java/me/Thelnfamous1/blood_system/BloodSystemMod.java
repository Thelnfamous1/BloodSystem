package me.Thelnfamous1.blood_system;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.blood_system.client.BloodSystemModClient;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
import me.Thelnfamous1.blood_system.common.datagen.BloodSystemDatagen;
import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import me.Thelnfamous1.blood_system.common.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(BloodSystemMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BloodSystemMod {
    public static final String MODID = "blood_system";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab BLOOD_SYSTEM_TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return ModItems.SYRINGE.get().getDefaultInstance();
        }
    };

    public BloodSystemMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BloodSystemConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BloodSystemConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BloodSystemConfig.commonSpec);
        modEventBus.register(BloodSystemConfig.class);
        modEventBus.register(BloodSystemDatagen.class);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModMobEffects.MOB_EFFECTS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(BloodCapabilityProvider.class);
        MinecraftForge.EVENT_BUS.register(BloodSystemCommands.class);
        if(FMLEnvironment.dist.isClient()){
            BloodSystemModClient.init(modEventBus);
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Do something when the setup is run on both client and server
        event.enqueueWork(BloodSystemNetwork::init);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event){
        event.add(EntityType.PLAYER, ModAttributes.MAX_BLOOD.get());
    }

    public static ResourceLocation location(String path){
        return new ResourceLocation(MODID, path);
    }

    public static String translationKeySuffixed(String suffix){
        return translationKey(null, suffix);
    }

    public static String translationKeyPrefixed(String prefix){
        return translationKey(prefix, null);
    }

    public static String translationKey(@Nullable String prefix, @Nullable String suffix){
        if(prefix != null && suffix != null){
            return prefix + "." + MODID + "." + suffix;
        } else if(prefix == null && suffix != null){
            return MODID + "." + suffix;
        } else if(prefix != null){
            return prefix + "." + MODID;
        } else{
            return MODID;
        }
    }
}
