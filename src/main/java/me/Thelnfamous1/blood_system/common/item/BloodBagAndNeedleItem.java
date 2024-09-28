package me.Thelnfamous1.blood_system.common.item;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.capability.BloodCapability;
import me.Thelnfamous1.blood_system.common.effect.BloodEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BloodBagAndNeedleItem extends BloodBagItem{

    public static final int TRANSFUSION_DURATION_SECONDS = 45;

    public BloodBagAndNeedleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean isUseable() {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 80;
    }

    @Override
    protected void injectCompatibleBlood(Player player, BloodCapability cap) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TRANSFUSION_DURATION_SECONDS * BloodEffect.getTransfusionEffectTickInterval()));
        player.addEffect(new MobEffectInstance(BloodSystemMod.TRANSFUSION.get(), TRANSFUSION_DURATION_SECONDS * BloodEffect.getTransfusionEffectTickInterval()));
    }

    @Override
    protected void extractBlood(Player player, BloodCapability cap) {
        cap.loseBlood(BloodEffect.getTransfusionBloodGainPerEffectTick() * TRANSFUSION_DURATION_SECONDS);
    }
}
