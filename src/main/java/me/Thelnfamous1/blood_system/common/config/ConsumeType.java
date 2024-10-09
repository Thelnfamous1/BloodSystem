package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum ConsumeType implements StringRepresentable {
    RIGHT_CLICK("right_click"),
    FINISH("finish");

    public static final Codec<ConsumeType> CODEC = StringRepresentable.fromEnum(ConsumeType::values);
    private final String name;

    ConsumeType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

}
