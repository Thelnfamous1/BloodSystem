package me.Thelnfamous1.blood_system.common.util;

import net.minecraft.world.item.TooltipFlag;

public class TooltipFlagWrapper implements TooltipFlag, CustomTooltipFlag {
    public static final TooltipFlagWrapper WRAPPED_NORMAL = new TooltipFlagWrapper(Default.NORMAL, false);
    public static final TooltipFlagWrapper WRAPPED_NORMAL_CREATIVE = new TooltipFlagWrapper(Default.NORMAL, true);
    public static final TooltipFlagWrapper WRAPPED_ADVANCED = new TooltipFlagWrapper(Default.ADVANCED, false);
    public static final TooltipFlagWrapper WRAPPED_ADVANCED_CREATIVE = new TooltipFlagWrapper(Default.ADVANCED, true);

    private final TooltipFlag wrapped;
    private final boolean isCreative;

    public TooltipFlagWrapper(TooltipFlag tooltipFlag, boolean isCreative){
        this.wrapped = tooltipFlag;
        this.isCreative = isCreative;
    }

    @Override
    public boolean isCreative() {
        return this.isCreative;
    }

    @Override
    public boolean isAdvanced() {
        return this.wrapped.isAdvanced();
    }

    public static TooltipFlagWrapper getOrCreateWrapper(TooltipFlag tooltipFlag, boolean isCreative){
        if(tooltipFlag == Default.NORMAL){
            return isCreative ? WRAPPED_NORMAL_CREATIVE : WRAPPED_NORMAL;
        } else if(tooltipFlag == Default.ADVANCED){
            return isCreative ? WRAPPED_ADVANCED_CREATIVE : WRAPPED_ADVANCED;
        } else{
            return new TooltipFlagWrapper(tooltipFlag, isCreative);
        }
    }

    public TooltipFlag getWrapped(){
        return this.wrapped;
    }
}
