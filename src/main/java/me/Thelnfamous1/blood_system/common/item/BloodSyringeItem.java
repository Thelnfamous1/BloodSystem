package me.Thelnfamous1.blood_system.common.item;

import me.Thelnfamous1.blood_system.common.capability.BloodCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

public class BloodSyringeItem extends BloodFillableItem {

    public BloodSyringeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean isUseable() {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 40;
    }

    protected void extractBlood(Player player, BloodCapability cap) {
        if(!player.getAbilities().invulnerable){
            cap.loseBlood(getContainableBlood());
        }
    }

    private static float getContainableBlood() {
        return 15.0F;
    }

    protected void injectCompatibleBlood(Player player, BloodCapability cap) {
        cap.gainBlood(getContainableBlood());
    }

}
