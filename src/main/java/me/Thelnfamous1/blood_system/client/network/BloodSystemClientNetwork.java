package me.Thelnfamous1.blood_system.client.network;

import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BloodSystemClientNetwork {

    public static void handleBloodSync(int id, CompoundTag capNbt) {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity instanceof Player player){
            BloodCapabilityProvider.getCapability(player).ifPresent(cap -> cap.deserializeNBT(capNbt));
        }
    }
}
