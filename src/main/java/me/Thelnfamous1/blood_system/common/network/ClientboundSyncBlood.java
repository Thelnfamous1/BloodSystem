package me.Thelnfamous1.blood_system.common.network;

import me.Thelnfamous1.blood_system.client.network.BloodSystemClientNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundSyncBlood(int id, CompoundTag capNbt) {

    public static ClientboundSyncBlood decode(FriendlyByteBuf buf){
        return new ClientboundSyncBlood(buf.readVarInt(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeVarInt(this.id);
        buf.writeNbt(this.capNbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> BloodSystemClientNetwork.handleBloodSync(this.id, this.capNbt));
        ctx.get().setPacketHandled(true);
    }
}
