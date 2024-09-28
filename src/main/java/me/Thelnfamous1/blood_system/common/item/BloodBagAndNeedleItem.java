package me.Thelnfamous1.blood_system.common.item;

import me.Thelnfamous1.blood_system.common.capability.BloodCapability;
import me.Thelnfamous1.blood_system.common.effect.BloodEffect;
import me.Thelnfamous1.blood_system.common.registries.ModMobEffects;
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
        int transfusionDuration = TRANSFUSION_DURATION_SECONDS * BloodEffect.getTransfusionEffectTickInterval();
        MobEffectInstance existingTransfusion = player.getEffect(ModMobEffects.TRANSFUSION.get());
        if(existingTransfusion != null){
            transfusionDuration += existingTransfusion.getDuration();
        }
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, transfusionDuration));
        player.addEffect(new MobEffectInstance(ModMobEffects.TRANSFUSION.get(), transfusionDuration));
        player.getCooldowns().addCooldown(this, transfusionDuration);
    }

    @Override
    protected void extractBlood(Player player, BloodCapability cap) {
        if(!player.getAbilities().invulnerable){
            cap.loseBlood(BloodEffect.getTransfusionBloodGainPerEffectTick() * TRANSFUSION_DURATION_SECONDS);
        }
    }
}
