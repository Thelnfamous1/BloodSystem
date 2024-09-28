package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.effect.BloodEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BloodSystemMod.MODID);
    public static final RegistryObject<MobEffect> TRANSFUSION = MOB_EFFECTS.register("transfusion", () -> new BloodEffect(MobEffectCategory.BENEFICIAL, 0x9c1515));
    public static final RegistryObject<MobEffect> CIRCULATION = MOB_EFFECTS.register("circulation", () -> new BloodEffect(MobEffectCategory.BENEFICIAL, 0x8a0303));
    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", () -> new BloodEffect(MobEffectCategory.HARMFUL, 0x640d0d));
}
