package me.Thelnfamous1.blood_system.common.network;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class BloodSystemNetwork {

    private static final ResourceLocation CHANNEL_NAME = BloodSystemMod.location("sync_channel");
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME, () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int INDEX;

    public static void init(){
        SYNC_CHANNEL.registerMessage(INDEX++, ServerboundRequestBloodSync.class, ServerboundRequestBloodSync::encode, ServerboundRequestBloodSync::decode, ServerboundRequestBloodSync::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        SYNC_CHANNEL.registerMessage(INDEX++, ClientboundSyncBlood.class, ClientboundSyncBlood::encode, ClientboundSyncBlood::decode, ClientboundSyncBlood::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
