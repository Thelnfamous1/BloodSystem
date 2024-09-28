package me.Thelnfamous1.blood_system.common.item;

import me.Thelnfamous1.blood_system.common.capability.BloodCapability;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import me.Thelnfamous1.blood_system.common.util.CustomTooltipFlag;
import me.Thelnfamous1.blood_system.mixin.LivingEntityAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class BloodFillableItem extends Item {
    public static final String BLOOD_DATA_TAG_KEY = "BloodData";
    public static final String BLOOD_TYPE_TAG_KEY = "BloodType";
    public static final String ANALYZED_TAG_KEY = "Analyzed";

    public BloodFillableItem(Properties pProperties) {
        super(pProperties);
    }

    public static void createAndGiveFilledContainer(ItemStack pStack, Player player, @Nullable BloodType bloodType) {
        if(player.getAbilities().instabuild){
            pStack = pStack.copy();
        }
        ItemStack split = pStack.split(1);
        BloodFillableItem.setStoredBloodType(split, bloodType);
        if(bloodType == null){
            BloodFillableItem.setAnalyzed(split, false);
        }
        if(!player.getInventory().add(split)){
            player.drop(split, false);
        }
    }

    public static void createAndGiveEmptyContainer(ItemStack pStack, Player player) {
        BloodFillableItem.createAndGiveFilledContainer(pStack, player, null);
    }

    public static Optional<BloodType> getStoredBloodType(ItemStack stack){
        CompoundTag bloodData = stack.getTagElement(BLOOD_DATA_TAG_KEY);
        if(bloodData == null){
            return Optional.empty();
        }
        BloodType byName = BloodType.byName(bloodData.getString(BLOOD_TYPE_TAG_KEY));
        // Correct stored data
        if(byName == null){
            // Get legacy stored data
            BloodType byOrdinal = BloodType.byOrdinal(bloodData.getByte(BLOOD_TYPE_TAG_KEY));
            if(byOrdinal != null){
                byName = byOrdinal;
                // Update from legacy
                setStoredBloodType(stack, byOrdinal);
            }
        }
        return Optional.ofNullable(byName);
    }

    public static void setStoredBloodType(ItemStack stack, @Nullable BloodType bloodType){
        if(bloodType == null && stack.getTagElement(BLOOD_DATA_TAG_KEY) == null){
            return;
        }
        CompoundTag bloodData = stack.getOrCreateTagElement(BLOOD_DATA_TAG_KEY);
        if(bloodData.contains(BLOOD_TYPE_TAG_KEY) && bloodType == null){
            bloodData.remove(BLOOD_TYPE_TAG_KEY);
            if(bloodData.isEmpty()){
                stack.removeTagKey(BLOOD_DATA_TAG_KEY);
            }
        } else if(bloodType != null){
            bloodData.putString(BLOOD_TYPE_TAG_KEY, bloodType.getSerializedName());
        }
    }

    public static boolean isAnalyzed(ItemStack stack){
        CompoundTag bloodData = stack.getTagElement(BLOOD_DATA_TAG_KEY);
        if(bloodData == null){
            return false;
        }
        return bloodData.contains(ANALYZED_TAG_KEY, Tag.TAG_ANY_NUMERIC) && bloodData.getBoolean(ANALYZED_TAG_KEY);
    }

    public static void setAnalyzed(ItemStack stack, boolean analyzed){
        if(!analyzed && stack.getTagElement(BLOOD_DATA_TAG_KEY) == null){
            return;
        }
        CompoundTag bloodData = stack.getOrCreateTagElement(BLOOD_DATA_TAG_KEY);
        if(bloodData.contains(ANALYZED_TAG_KEY) && !analyzed){
            bloodData.remove(ANALYZED_TAG_KEY);
            if(bloodData.isEmpty()){
                stack.removeTagKey(BLOOD_DATA_TAG_KEY);
            }
        } else if(analyzed){
            bloodData.putBoolean(ANALYZED_TAG_KEY, true);
        }
    }

    protected abstract boolean isUseable();

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        if(this.isUseable()){
            pPlayer.startUsingItem(pUsedHand);
            this.playInjectionStartSound(pLevel, pPlayer, pUsedHand, itemInHand);
            return InteractionResultHolder.consume(itemInHand);
        } else{
            return InteractionResultHolder.pass(itemInHand);
        }
    }

    protected void playInjectionStartSound(Level pLevel, LivingEntity user, InteractionHand hand, ItemStack stack) {
        pLevel.playSound(null, user.getX(), user.getY(), user.getZ(), ((LivingEntityAccessor)user).blood_system$callGetHurtSound(DamageSource.GENERIC), user.getSoundSource(), 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        if(this.isUseable()){
            return UseAnim.BLOCK;
        } else{
            return UseAnim.NONE;
        }
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int useItemRemaining) {
        if(this.useOnRelease(pStack)){
            int useDuration = this.getUseDuration(pStack);
            int useTime = useDuration - useItemRemaining;
            if(useTime / useDuration >= 1){
                this.completeUsing(pStack, pLevel, pLivingEntity);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        this.completeUsing(pStack, pLevel, pLivingEntity);
        return pStack;
    }

    protected void completeUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if(this.isUseable()){
            if(pLivingEntity instanceof Player player){
                if(!pLevel.isClientSide){
                    BloodCapabilityProvider.getCapability(player).ifPresent(cap -> {
                        BloodType playerBloodType = cap.getBloodType();
                        getStoredBloodType(pStack).ifPresentOrElse(bt -> {
                            createAndGiveEmptyContainer(pStack, player);
                            this.playInjectionFinishSound(pLevel, pLivingEntity, pLivingEntity.getUsedItemHand(), pStack);
                            if(bt.canDonateTo(playerBloodType)){
                                this.injectCompatibleBlood(player, cap);
                            } else{
                                this.injectIncompatibleBlood(player, cap);
                            }
                        }, () -> {
                            createAndGiveFilledContainer(pStack, player, playerBloodType);
                            this.playExtractionFinishSound(pLevel, pLivingEntity, pLivingEntity.getUsedItemHand(), pStack);
                            this.extractBlood(player, cap);
                        });
                    });
                    this.onUseCompleted(pStack, pLevel, player);
                }
            }
        }
    }

    protected void playInjectionFinishSound(Level pLevel, LivingEntity user, InteractionHand hand, ItemStack stack) {
        pLevel.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_EMPTY, user.getSoundSource(), 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    protected void playExtractionFinishSound(Level pLevel, LivingEntity user, InteractionHand hand, ItemStack stack) {
        pLevel.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL, user.getSoundSource(), 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    protected void injectIncompatibleBlood(Player player, BloodCapability cap) {
        if(!player.getAbilities().invulnerable){
            player.kill();
        }
    }

    protected void extractBlood(Player player, BloodCapability cap) {
    }

    protected void injectCompatibleBlood(Player player, BloodCapability cap) {
    }

    protected void onUseCompleted(ItemStack stack, Level pLevel, Player player) {
        player.getCooldowns().addCooldown(stack.getItem(), 10);
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            pItems.add(new ItemStack(this));
            for(BloodType bloodType : BloodType.values()){
                ItemStack filled = new ItemStack(this);
                setStoredBloodType(filled, bloodType);
                pItems.add(filled);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        boolean analyzed = isAnalyzed(pStack);
        boolean creative = pIsAdvanced instanceof CustomTooltipFlag customTooltipFlag && customTooltipFlag.isCreative();
        if(analyzed || creative){
            getStoredBloodType(pStack).ifPresent(bloodType -> {
                MutableComponent bloodTypeTooltip = CommonComponents.optionNameValue(BloodType.getCaption(), bloodType.getDisplayName());
                if(!analyzed){
                    bloodTypeTooltip.withStyle(ChatFormatting.ITALIC);
                }
                pTooltipComponents.add(bloodTypeTooltip);
            });
        }
    }
}
