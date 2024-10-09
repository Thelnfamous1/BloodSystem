package me.Thelnfamous1.blood_system.client;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.client.screen.BloodAnalyzerScreen;
import me.Thelnfamous1.blood_system.client.screen.MicroscopeScreen;
import me.Thelnfamous1.blood_system.common.item.BloodFillableItem;
import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import me.Thelnfamous1.blood_system.common.network.ServerboundRequestBloodSync;
import me.Thelnfamous1.blood_system.common.registries.ModItems;
import me.Thelnfamous1.blood_system.common.registries.ModMenuTypes;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class BloodSystemModClient {
    public static final ResourceLocation BLOOD_FILLED_ITEM_PROPERTY = BloodSystemMod.location("blood_filled");

    public static void init(IEventBus modEventBus) {
        MinecraftForge.EVENT_BUS.register(GameplayEvents.class);
        modEventBus.register(ModloadingEvents.class);
    }

    public static class GameplayEvents{
        @SubscribeEvent
        public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event){
            if(DebugFlags.DEBUG_BLOOD_CAP_SYNC){
                BloodSystemMod.LOGGER.info("Sending request to server to sync blood cap for {}!", event.getPlayer());
            }
            BloodSystemNetwork.SYNC_CHANNEL.sendToServer(new ServerboundRequestBloodSync());
        }
    }

    public static class ModloadingEvents{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ItemProperties.register(ModItems.SYRINGE.get(), BLOOD_FILLED_ITEM_PROPERTY, ModloadingEvents::getBloodFilled));
            event.enqueueWork(() -> ItemProperties.register(ModItems.BLOOD_BAG.get(), BLOOD_FILLED_ITEM_PROPERTY, ModloadingEvents::getBloodFilled));
            event.enqueueWork(() -> ItemProperties.register(ModItems.BLOOD_BAG_AND_NEEDLE.get(), BLOOD_FILLED_ITEM_PROPERTY, ModloadingEvents::getBloodFilled));
            event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.BLOOD_ANALYZER.get(), BloodAnalyzerScreen::new));
            event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.MICROSCOPE.get(), MicroscopeScreen::new));
        }

        private static float getBloodFilled(ItemStack pStack, ClientLevel level, LivingEntity entity, long seed) {
            if(BloodFillableItem.getStoredBloodType(pStack).isPresent()){
                return 1.0F;
            } else{
                return 0.0F;
            }
        }

        @SubscribeEvent
        public static void onRegisterGUIOverlay(RegisterGuiOverlaysEvent event){
            event.registerBelow(VanillaGuiOverlay.PLAYER_HEALTH.id(), "blood", BloodMeterOverlay.INSTANCE);
        }
    }
}
