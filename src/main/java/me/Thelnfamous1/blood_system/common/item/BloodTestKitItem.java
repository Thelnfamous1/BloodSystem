package me.Thelnfamous1.blood_system.common.item;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import me.Thelnfamous1.blood_system.common.registries.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BloodTestKitItem extends Item {
    public static final String ANALYZED = BloodSystemMod.translationKey("item", ModItems.BLOOD_TEST_KIT.getId().getPath() + ".analyzed");
    public static final String NOTHING = BloodSystemMod.translationKey("item", ModItems.BLOOD_TEST_KIT.getId().getPath() + ".nothing");
    public static final String TOOLTIP = BloodSystemMod.translationKey("item", ModItems.BLOOD_TEST_KIT.getId().getPath() + ".tooltip");

    public BloodTestKitItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack usedStack = pPlayer.getItemInHand(pUsedHand);
        ItemStack otherStack = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if(!otherStack.isEmpty()){
            if(BloodFillableItem.isAnalyzed(otherStack)){
                if(pPlayer.level.isClientSide) {
                    pPlayer.displayClientMessage(Component.translatable(ANALYZED, otherStack.getDisplayName()).withStyle(ChatFormatting.RED), true);
                }
                return InteractionResultHolder.pass(usedStack);
            } else{
                Optional<BloodType> storedBloodType = BloodFillableItem.getStoredBloodType(otherStack);
                if(storedBloodType.isPresent()){
                    BloodFillableItem.setAnalyzed(otherStack, true);
                    pPlayer.awardStat(Stats.ITEM_USED.get(this));
                    if (!pPlayer.getAbilities().instabuild) {
                        usedStack.shrink(1);
                    }
                    pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                    return InteractionResultHolder.sidedSuccess(usedStack, pLevel.isClientSide);
                }
            }
        }
        if(pPlayer.level.isClientSide){
            pPlayer.displayClientMessage(Component.translatable(NOTHING).withStyle(ChatFormatting.RED), true);
        }
        return InteractionResultHolder.pass(usedStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(TOOLTIP).withStyle(ChatFormatting.GRAY));
    }
}
