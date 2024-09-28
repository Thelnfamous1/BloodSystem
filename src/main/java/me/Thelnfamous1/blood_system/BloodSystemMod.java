package me.Thelnfamous1.blood_system;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.blood_system.client.BloodSystemModClient;
import me.Thelnfamous1.blood_system.common.block.BloodAnalyzerBlock;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
import me.Thelnfamous1.blood_system.common.datagen.BloodSystemDatagen;
import me.Thelnfamous1.blood_system.common.effect.BloodEffect;
import me.Thelnfamous1.blood_system.common.item.BloodBagAndNeedleItem;
import me.Thelnfamous1.blood_system.common.item.BloodBagItem;
import me.Thelnfamous1.blood_system.common.item.BloodPillItem;
import me.Thelnfamous1.blood_system.common.item.BloodSyringeItem;
import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.ToIntFunction;

@Mod(BloodSystemMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BloodSystemMod {
    public static final String MODID = "blood_system";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

    public static final RegistryObject<Attribute> MAX_BLOOD = ATTRIBUTES.register("blood", () -> new RangedAttribute(translationKey("attribute", "max_blood"), 100.0D, 1.0D, Float.MAX_VALUE).setSyncable(true));

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", () -> new BloodEffect(MobEffectCategory.HARMFUL, 0x640d0d));
    public static final RegistryObject<MobEffect> CIRCULATION = MOB_EFFECTS.register("circulation", () -> new BloodEffect(MobEffectCategory.BENEFICIAL, 0x8a0303));
    public static final RegistryObject<MobEffect> TRANSFUSION = MOB_EFFECTS.register("transfusion", () -> new BloodEffect(MobEffectCategory.BENEFICIAL, 0x9c1515));

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> BLOOD_ANALYZER = BLOCKS.register("blood_analyzer", () -> new BloodAnalyzerBlock(
            BlockBehaviour.Properties.of(Material.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .lightLevel(litBlockEmission(13))));

    private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
        return (state) -> state.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
    }
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final CreativeModeTab BLOOD_SYSTEM_TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return SYRINGE.get().getDefaultInstance();
        }
    };

    public static final RegistryObject<Item> VEINAMITOL = ITEMS.register("veinamitol", () -> new BloodPillItem(new Item.Properties().tab(BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> NEEDLE = ITEMS.register("needle", () -> new Item(new Item.Properties().tab(BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> BLOOD_BAG = ITEMS.register("blood_bag", () -> new BloodBagItem(new Item.Properties().tab(BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> BLOOD_BAG_AND_NEEDLE = ITEMS.register("blood_bag_and_needle", () -> new BloodBagAndNeedleItem(new Item.Properties().tab(BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe", () -> new BloodSyringeItem(new Item.Properties().tab(BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> BLOOD_ANALYZER_ITEM = ITEMS.register("blood_analyzer", () -> new BlockItem(BLOOD_ANALYZER.get(), new Item.Properties().tab(BLOOD_SYSTEM_TAB)));

    public BloodSystemMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BloodSystemConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BloodSystemConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BloodSystemConfig.commonSpec);
        modEventBus.register(BloodSystemConfig.class);
        modEventBus.register(BloodSystemDatagen.class);
        ATTRIBUTES.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
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
        event.add(EntityType.PLAYER, MAX_BLOOD.get());
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
