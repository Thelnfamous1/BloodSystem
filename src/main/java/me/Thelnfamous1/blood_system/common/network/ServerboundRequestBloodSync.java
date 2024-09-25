package me.Thelnfamous1.blood_system.common.network;

import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record ServerboundRequestBloodSync() {

    public static ServerboundRequestBloodSync decode(FriendlyByteBuf buf){
        return new ServerboundRequestBloodSync();
    }

    public void encode(FriendlyByteBuf buf){
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            BloodCapabilityProvider.getCapability(sender).ifPresent(cap -> BloodSystemNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new ClientboundSyncBlood(sender.getId(), cap.serializeNBT())));
        });
        ctx.get().setPacketHandled(true);
    }
}
