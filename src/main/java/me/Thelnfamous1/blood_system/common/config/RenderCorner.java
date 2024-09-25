package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum RenderCorner implements StringRepresentable {
    TOP_LEFT("top_left", 1, 1),
    TOP_RIGHT("top_right", -1, 1),
    BOTTOM_LEFT("bottom_left", 1, -1),
    BOTTOM_RIGHT("bottom_right", -1, -1);

    public static final Codec<RenderCorner> CODEC = StringRepresentable.fromEnum(RenderCorner::values);

    private final String name;
    private final int xScale;
    private final int yScale;

    RenderCorner(String name, int xScale, int yScale) {
        this.name = name;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int scaleXOffset(int xOffset){
        return xOffset * this.xScale;
    }

    public int scaleYOffset(int yOffset){
        return yOffset * this.yScale;
    }
}
