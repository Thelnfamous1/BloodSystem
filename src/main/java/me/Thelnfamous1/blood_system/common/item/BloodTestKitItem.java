package me.Thelnfamous1.blood_system.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BloodTestKitItem extends Item {
    public BloodTestKitItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack usedStack = pPlayer.getItemInHand(pUsedHand);
        ItemStack otherStack = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if(!otherStack.isEmpty() && !BloodFillableItem.isAnalyzed(otherStack)){
            return BloodFillableItem.getStoredBloodType(otherStack)
                    .map(bloodType -> {
                        BloodFillableItem.setAnalyzed(otherStack, true);
                        pPlayer.awardStat(Stats.ITEM_USED.get(this));
                        if (!pPlayer.getAbilities().instabuild) {
                            usedStack.shrink(1);
                        }
                        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                        return InteractionResultHolder.sidedSuccess(usedStack, pLevel.isClientSide);
                    })
                    .orElse(InteractionResultHolder.pass(usedStack));
        }
        return InteractionResultHolder.pass(usedStack);
    }
}
