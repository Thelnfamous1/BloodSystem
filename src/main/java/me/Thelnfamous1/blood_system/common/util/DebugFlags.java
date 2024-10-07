package me.Thelnfamous1.blood_system.common.util;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class DebugFlags {
    public static final boolean DEBUG_BLOOD_CAP_SYNC = !FMLEnvironment.production;
    public static final boolean DEBUG_PASSIVE_BLOOD_REGENERATION = !FMLEnvironment.production;
    public static final boolean DEBUG_BLOOD_LOSS_EFFECTS = !FMLEnvironment.production;
    public static final boolean DEBUG_BLOOD_LOSS_TAKEN_DAMAGE = !FMLEnvironment.production;
    public static final boolean DEBUG_BLOOD_ANALYZER = !FMLEnvironment.production;
    public static final boolean DEBUG_BATTERY_CHARGES = !FMLEnvironment.production;
}
