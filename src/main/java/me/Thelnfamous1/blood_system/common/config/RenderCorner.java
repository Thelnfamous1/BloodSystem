package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum RenderCorner implements StringRepresentable {
    TOP_LEFT("top_left", true, true),
    TOP_RIGHT("top_right", true, false),
    BOTTOM_LEFT("bottom_left", false, true),
    BOTTOM_RIGHT("bottom_right", false, false);

    public static final Codec<RenderCorner> CODEC = StringRepresentable.fromEnum(RenderCorner::values);

    private final String name;
    private final boolean top;
    private final boolean left;

    RenderCorner(String name, boolean top, boolean left) {
        this.name = name;
        this.top = top;
        this.left = left;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getXPos(int xOffset, int screenWidth){
        if(this.left){
            return xOffset;
        } else{
            return screenWidth - xOffset;
        }
    }

    public int getYPos(int yOffset, int screenHeight){
        if(this.top){
            return yOffset;
        } else{
            return screenHeight - yOffset;
        }
    }
}
