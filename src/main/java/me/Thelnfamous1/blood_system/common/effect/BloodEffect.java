package me.Thelnfamous1.blood_system.common.effect;

import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
import me.Thelnfamous1.blood_system.common.registries.ModMobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BloodEffect extends MobEffect {
    public BloodEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static float getTransfusionBloodGainPerEffectTick() {
        return 0.78F;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        if(this == ModMobEffects.BLEEDING.get()){
            int bleedingEffectTickInterval = getBleedingEffectTickInterval();
            return bleedingEffectTickInterval <= 0 || pDuration % bleedingEffectTickInterval == 0;
        } else if(this == ModMobEffects.TRANSFUSION.get()){
            int transfusionEffectTickInterval = getTransfusionEffectTickInterval();
            return transfusionEffectTickInterval <= 0 || pDuration % transfusionEffectTickInterval == 0;
        } else {
            return false;
        }
    }

    private static int getBleedingEffectTickInterval() {
        return Mth.floor(BloodSystemConfig.SERVER.bleedFrequency.get() * 20);
    }

    public static int getTransfusionEffectTickInterval() {
        return 20;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            if(this == ModMobEffects.BLEEDING.get() && !player.getAbilities().invulnerable){
                BloodCapabilityProvider.getCapability(player).ifPresent(cap -> cap.loseBlood(BloodSystemConfig.SERVER.bleedAmount.get().floatValue()));
            } else if(this == ModMobEffects.TRANSFUSION.get()){
                BloodCapabilityProvider.getCapability(player).ifPresent(cap -> cap.gainBlood(getTransfusionBloodGainPerEffectTick()));
            }
        }
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
