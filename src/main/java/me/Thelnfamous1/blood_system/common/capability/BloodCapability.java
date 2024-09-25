package me.Thelnfamous1.blood_system.common.capability;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
import me.Thelnfamous1.blood_system.common.config.MobEffectData;
import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import me.Thelnfamous1.blood_system.common.network.ClientboundSyncBlood;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoRegisterCapability
public interface BloodCapability extends INBTSerializable<CompoundTag> {

    String BLOOD_TAG_KEY = "Blood";
    String BLOOD_TYPE_TAG_KEY = "BloodType";

    void setPlayer(@Nullable Player player);

    float getBlood();

    void setBlood(float blood);

    @Nullable
    BloodType getBloodType();

    void setBloodType(@Nullable BloodType bloodType);

    void tick();

    void hurt(float damageAmount);

    default void loseBlood(float amount){
        this.setBlood(this.getBlood() - amount);
    }

    default void gainBlood(float amount){
        this.setBlood(this.getBlood() + amount);
    }

    default void invalidate(){
        this.setPlayer(null);
    }

    default void copy(BloodCapability other, boolean death){
        CompoundTag nbt = other.serializeNBT();
        if(death){
            nbt.remove(BLOOD_TAG_KEY);
        }
        this.deserializeNBT(nbt);
    }

    @Override
    default CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(BLOOD_TAG_KEY, this.getBlood());
        BloodType bloodType = this.getBloodType();
        if(bloodType != null){
            tag.putByte(BLOOD_TYPE_TAG_KEY, (byte) bloodType.ordinal());
        }
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains(BLOOD_TAG_KEY, Tag.TAG_FLOAT)){
            this.setBlood(nbt.getFloat(BLOOD_TAG_KEY));
        }
        if(nbt.contains(BLOOD_TYPE_TAG_KEY, Tag.TAG_BYTE)){
            this.setBloodType(BloodType.byOrdinal(nbt.getByte(BLOOD_TYPE_TAG_KEY)));
        }
    }

    class Implementation implements BloodCapability {
        public static final float FULL_HEART = 2.0F;
        private float blood;
        @Nullable
        private Player player;
        @Nullable
        private BloodType bloodType;
        private List<MobEffectData> activeBloodLossEffects = List.of();

        @Override
        public void setPlayer(Player player) {
            if(this.player == null) {
                this.player = player;
                if(!this.player.level.isClientSide){
                    if(this.bloodType == null){ // We are a new player with no assigned blood type, so set it and set our blood to max
                        this.setBloodType(BloodType.getRandom(this.player.getRandom()));
                        this.setBlood(this.getMaxBlood());
                    } else{ // We are an existing player with an assigned blood type, so just refresh our active blood loss effects
                        this.refreshActiveBloodLossEffects();
                    }
                }
            }
        }

        @Override
        public float getBlood() {
            return this.blood;
        }

        @Override
        public void setBlood(float blood) {
            float lastBlood = this.blood;
            if(this.player == null){
                this.blood = Math.max(0.0F, blood);
            } else{
                this.blood = Mth.clamp(blood, 0.0F, this.getMaxBlood());
            }
            if(this.blood <= 0.0F && this.player != null && !this.player.level.isClientSide){
                this.player.kill();
            }
            if(this.player != null && !this.player.level.isClientSide){
                this.refreshActiveBloodLossEffects();
                if(lastBlood != this.blood){
                    BloodSystemNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.player),
                            new ClientboundSyncBlood(this.player.getId(), this.serializeNBT()));
                }
            }
        }

        private void refreshActiveBloodLossEffects() {
            this.activeBloodLossEffects = this.getBloodLossEffects(this.getBloodPercentage());
            BloodSystemMod.LOGGER.info("Set activeBloodLossEffects for {}: {}", this.player, this.activeBloodLossEffects.stream().map(MobEffectData::asPair).toList());
        }

        private List<MobEffectData> getBloodLossEffects(float bloodPercentage) {
            return BloodSystemConfig.SERVER.bloodLossEffects.get()
                    .tailMap(Mth.floor(bloodPercentage))
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .toList();
        }

        public final float getMaxBlood() {
            return (float)this.player.getAttributeValue(BloodSystemMod.MAX_BLOOD.get());
        }

        public final float getBloodPercentage(){
            return (this.blood / this.getMaxBlood()) * 100;
        }

        public final boolean isInjured(){
            return this.blood < this.getMaxBlood();
        }

        @Override
        @Nullable
        public BloodType getBloodType() {
            return this.bloodType;
        }

        @Override
        public void setBloodType(BloodType bloodType) {
            this.bloodType = bloodType;
            if(this.player != null && !this.player.level.isClientSide){
                BloodSystemNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.player),
                        new ClientboundSyncBlood(this.player.getId(), this.serializeNBT()));
            }
        }

        @Override
        public void tick(){
            if(this.player != null){
                if(!this.player.level.isClientSide){
                    // tick passive blood regeneration
                    if(this.isInjured() && this.player.getFoodData().getFoodLevel() >= BloodSystemConfig.SERVER.bloodRegenMinFoodLevel.get()){
                        int bloodRegenFreqInTicks = Mth.floor(BloodSystemConfig.SERVER.bloodRegenFrequency.get() * 20);
                        if(bloodRegenFreqInTicks > 0 && this.player.tickCount % bloodRegenFreqInTicks == 0){
                            this.gainBlood(BloodSystemConfig.SERVER.bloodRegenAmount.get().floatValue());
                            BloodSystemMod.LOGGER.info("{} regenerated {} blood!", this.player, BloodSystemConfig.SERVER.bloodRegenAmount.get().floatValue());
                        }
                    }
                    // add active blood loss effects
                    if(this.player.tickCount % 20 == 0 && !this.activeBloodLossEffects.isEmpty()){
                        for(MobEffectData bloodLossEffect : this.activeBloodLossEffects){
                            // 21 ticks allows the effect to persist for at least 1 second before being removed and potentially re-added
                            this.player.addEffect(new MobEffectInstance(bloodLossEffect.effect(), 21, bloodLossEffect.amplifier()));
                        }
                    }
                }
            }
        }

        @Override
        public void hurt(float damageAmount) {
            if(this.player != null && !this.player.level.isClientSide){
                if(damageAmount >= 1.0F){
                    this.loseBlood(BloodSystemConfig.SERVER.bloodLossWhenTakingDamage.get().floatValue());
                    BloodSystemMod.LOGGER.info("{} lost {} blood!", this.player, BloodSystemConfig.SERVER.bloodLossWhenTakingDamage.get().floatValue());

                }
                if(damageAmount >= FULL_HEART){
                    float additionalDamageInFullHearts = (damageAmount - FULL_HEART) / FULL_HEART;
                    double additionalBleedChance = Math.max(0.0D, additionalDamageInFullHearts) * BloodSystemConfig.SERVER.bleedChanceWhenTakingDamageExtra.get();
                    double chance = BloodSystemConfig.SERVER.bleedChanceWhenTakingDamage.get() + additionalBleedChance;
                    if(this.player.getRandom().nextDouble() * 100.0F <= chance){
                        BloodSystemMod.LOGGER.info("{} now has the bleed status effect!", this.player);
                    }
                }
            }
        }

    }
}