package me.Thelnfamous1.blood_system.common.capability;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public class BloodCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<BloodCapability> BLOOD_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation IDENTIFIER = BloodSystemMod.location("blood");
    private final BloodCapability instance = new BloodCapability.Implementation();
    private final LazyOptional<BloodCapability> optional = LazyOptional.of(() -> this.instance);

    public static LazyOptional<BloodCapability> getCapability(Player player){
        LazyOptional<BloodCapability> capability = player.getCapability(BloodCapabilityProvider.BLOOD_CAPABILITY);
        capability.ifPresent(cap -> cap.setPlayer(player));
        return capability;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        if (capability == BLOOD_CAPABILITY) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.instance.deserializeNBT(nbt);
    }

    @SubscribeEvent
    public static void onAttachingCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        BloodCapabilityProvider provider = new BloodCapabilityProvider();
        event.addCapability(IDENTIFIER, provider);
        event.addListener(provider.instance::invalidate);
    }

    @SubscribeEvent
    public static void onClonePlayer(final PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        getCapability(event.getOriginal()).ifPresent(originalCap -> getCapability(event.getEntity()).ifPresent(newCap -> newCap.copy(originalCap, event.isWasDeath())));
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onTickPlayer(final TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            getCapability(event.player).ifPresent(BloodCapability::tick);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamagePlayer(final LivingDamageEvent event) {
        if(event.getEntity() instanceof Player player){
            getCapability(player).ifPresent(cap -> cap.hurt(event.getAmount()));
        }
    }
}
