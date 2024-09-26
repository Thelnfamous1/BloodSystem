package me.Thelnfamous1.blood_system.client;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.item.BloodSyringeItem;
import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import me.Thelnfamous1.blood_system.common.network.ServerboundRequestBloodSync;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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
            event.enqueueWork(() -> ItemProperties.register(BloodSystemMod.SYRINGE.get(), BLOOD_FILLED_ITEM_PROPERTY, (pStack, pLevel, pEntity, pSeed) -> {
                if(BloodSyringeItem.getStoredBloodType(pStack).isPresent()){
                    return 1.0F;
                } else{
                    return 0.0F;
                }
            }));
        }

        @SubscribeEvent
        public static void onRegisterGUIOverlay(RegisterGuiOverlaysEvent event){
            event.registerBelow(VanillaGuiOverlay.PLAYER_HEALTH.id(), "blood", BloodMeterOverlay.INSTANCE);
        }
    }
}
