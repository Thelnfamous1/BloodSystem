package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.menu.BloodAnalyzerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BloodSystemMod.MODID);

    public static final RegistryObject<MenuType<BloodAnalyzerMenu>> BLOOD_ANALYZER = MENU_TYPES.register("blood_analyzer", () -> new MenuType<>(BloodAnalyzerMenu::new));
}
