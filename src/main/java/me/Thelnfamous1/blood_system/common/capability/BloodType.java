package me.Thelnfamous1.blood_system.common.capability;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public enum BloodType implements StringRepresentable {
    A_POSITIVE("A+", AntigenType.A, AntibodyType.B, RhFactor.PRESENT),
    A_NEGATIVE("A-", AntigenType.A, AntibodyType.B, RhFactor.ABSENT),
    B_POSITIVE("B+", AntigenType.B, AntibodyType.A, RhFactor.PRESENT),
    B_NEGATIVE("B-", AntigenType.B, AntibodyType.A, RhFactor.ABSENT),
    AB_POSITIVE("AB+", AntigenType.AB, AntibodyType.NONE, RhFactor.PRESENT),
    AB_NEGATIVE("AB-", AntigenType.AB, AntibodyType.NONE, RhFactor.ABSENT),
    O_POSITIVE("O+", AntigenType.NONE, AntibodyType.AB, RhFactor.PRESENT),
    O_NEGATIVE("O-", AntigenType.NONE, AntibodyType.AB, RhFactor.ABSENT);

    public static final StringRepresentable.EnumCodec<BloodType> CODEC = StringRepresentable.fromEnum(BloodType::values);

    private final String name;
    private final AntigenType antigenType;
    private final AntibodyType antibodyType;
    private final RhFactor rhFactor;

    BloodType(String name, AntigenType antigenType, AntibodyType antibodyType, RhFactor rhFactor){
        this.name = name;
        this.antigenType = antigenType;
        this.antibodyType = antibodyType;
        this.rhFactor = rhFactor;
    }

    @Nullable
    public static BloodType read(CompoundTag tag, String key){
        if(tag.contains(key, Tag.TAG_STRING)){
            return BloodType.byName(tag.getString(key));
        } else if(tag.contains(key, Tag.TAG_ANY_NUMERIC)){
            return BloodType.byOrdinal(tag.getByte(key));
        }
        return null;
    }

    @Nullable
    public static BloodType byName(String pName) {
        return CODEC.byName(pName);
    }

    @Nullable
    public static BloodType byOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal > values().length) {
            return null;
        }

        return values()[ordinal];
    }

    public static BloodType getRandom(RandomSource randomSource) {
        return Util.getRandom(BloodType.values(), randomSource);
    }

    public static MutableComponent getCaption() {
        return Component.translatable(BloodSystemMod.translationKeyPrefixed("blood_type"));
    }

    public static MutableComponent getUnknownDisplayName() {
        return Component.translatable(BloodSystemMod.translationKey("blood_type", "unknown"));
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(this.getTranslationKey());
    }

    public String getTranslationKey() {
        return BloodSystemMod.translationKey("blood_type", this.name);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean canDonateTo(BloodType other){
        return other.antibodyType.canReceiveFrom(this.antigenType) && other.rhFactor.canReceiveFrom(this.rhFactor);
    }

    public boolean canReceiveFrom(BloodType other){
        return this.antibodyType.canReceiveFrom(other.antigenType) && this.rhFactor.canReceiveFrom(other.rhFactor);
    }

    public enum AntigenType {
        A(true, false),
        B(false, true),
        AB(true, true),
        NONE(false, false);

        private final boolean a;
        private final boolean b;

        AntigenType(boolean a, boolean b){
            this.a = a;
            this.b = b;
        }

        public boolean isA() {
            return this.a;
        }

        public boolean isB(){
            return this.b;
        }
    }

    public enum AntibodyType {
        A(true, false),
        B(false, true),
        AB(true, true),
        NONE(false, false);

        private final boolean a;
        private final boolean b;

        AntibodyType(boolean a, boolean b){
            this.a = a;
            this.b = b;
        }

        public boolean isA() {
            return this.a;
        }

        public boolean isB(){
            return this.b;
        }

        public boolean canReceiveFrom(AntigenType antigenType) {
            switch (this){
                case A -> {
                    return !antigenType.isA();
                }
                case B -> {
                    return !antigenType.isB();
                }
                case AB -> {
                    return !antigenType.isA() && !antigenType.isB();
                }
                default -> {
                    return true;
                }
            }
        }
    }

    public enum RhFactor {
        PRESENT,
        ABSENT;

        public boolean canReceiveFrom(RhFactor other){
            return other == ABSENT || this == PRESENT;
        }
    }
}
