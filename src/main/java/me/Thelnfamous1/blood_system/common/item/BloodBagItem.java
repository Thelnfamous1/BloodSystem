package me.Thelnfamous1.blood_system.common.item;

public class BloodBagItem extends BloodFillableItem {
    public BloodBagItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean isUseable() {
        return false;
    }
}
