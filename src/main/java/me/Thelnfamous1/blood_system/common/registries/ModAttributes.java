package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, BloodSystemMod.MODID);
    public static final RegistryObject<Attribute> MAX_BLOOD = ATTRIBUTES.register("blood", () -> new RangedAttribute(BloodSystemMod.translationKey("attribute", "max_blood"), 100.0D, 1.0D, Float.MAX_VALUE).setSyncable(true));
}
