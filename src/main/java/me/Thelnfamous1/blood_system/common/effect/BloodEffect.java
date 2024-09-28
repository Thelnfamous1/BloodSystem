package me.Thelnfamous1.blood_system.common.effect;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
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

    public static int getTransfusionEffectTickInterval() {
        return 20;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        if(this == BloodSystemMod.BLEEDING.get()){
            return pDuration % 600 == 0;
        } else if(this == BloodSystemMod.TRANSFUSION.get()){
            return pDuration % getTransfusionEffectTickInterval() == 0;
        } else {
            return false;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            if(this == BloodSystemMod.BLEEDING.get()){
                BloodCapabilityProvider.getCapability(player).ifPresent(cap -> cap.loseBlood(1.0F));
            } else if(this == BloodSystemMod.TRANSFUSION.get()){
                BloodCapabilityProvider.getCapability(player).ifPresent(cap -> cap.gainBlood(getTransfusionBloodGainPerEffectTick()));
            }
        }
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
