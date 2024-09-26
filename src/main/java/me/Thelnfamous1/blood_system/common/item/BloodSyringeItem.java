package me.Thelnfamous1.blood_system.common.item;

import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BloodSyringeItem extends Item {

    public static final String BLOOD_TYPE_TAG_KEY = "BloodType";

    public BloodSyringeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        playInjectionSound(pLevel, pPlayer);
        return InteractionResultHolder.consume(itemInHand);
    }

    private static void playInjectionSound(Level pLevel, Player player) {
        pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if(!pLevel.isClientSide && pLivingEntity instanceof Player player){
            BloodCapabilityProvider.getCapability(player).ifPresent(cap -> {
                BloodType playerBloodType = cap.getBloodType();
                getStoredBloodType(pStack).ifPresentOrElse(bt -> {
                    createAndGiveEmptySyringe(pStack, player);
                    if(bt.canDonateTo(playerBloodType)){
                        cap.gainBlood(15.0F);
                    } else{
                        player.kill();
                    }
                }, () -> {
                    createAndGiveFilledSyringe(pStack, player, playerBloodType);
                    cap.loseBlood(15.0F);
                });
            });
        }
        return pStack;
    }

    private static void createAndGiveFilledSyringe(ItemStack pStack, Player player, @Nullable BloodType playerBloodType) {
        if(player.isCreative()){
            pStack = pStack.copy();
        }
        ItemStack split = pStack.split(1);
        setStoredBloodType(split, playerBloodType);
        if(!player.getInventory().add(split)){
            player.drop(split, false);
        }
    }

    private static void createAndGiveEmptySyringe(ItemStack pStack, Player player) {
        createAndGiveFilledSyringe(pStack, player, null);
    }

    public static Optional<BloodType> getStoredBloodType(ItemStack stack){
        CompoundTag tag = stack.getTag();
        if(tag == null){
            return Optional.empty();
        }
        return Optional.ofNullable(tag.contains(BLOOD_TYPE_TAG_KEY, Tag.TAG_BYTE) ? tag.getByte(BLOOD_TYPE_TAG_KEY) : null)
                .map(BloodType::byOrdinal);
    }

    public static void setStoredBloodType(ItemStack stack, @Nullable BloodType bloodType){
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(BLOOD_TYPE_TAG_KEY, Tag.TAG_BYTE) && bloodType == null){
            tag.remove(BLOOD_TYPE_TAG_KEY);
            if(tag.isEmpty()){
                stack.setTag(null);
            }
        } else if(bloodType != null){
            tag.putByte(BLOOD_TYPE_TAG_KEY, (byte) bloodType.ordinal());
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

    }
}
