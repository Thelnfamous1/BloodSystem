package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.item.BloodBagAndNeedleItem;
import me.Thelnfamous1.blood_system.common.item.BloodBagItem;
import me.Thelnfamous1.blood_system.common.item.BloodPillItem;
import me.Thelnfamous1.blood_system.common.item.BloodSyringeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BloodSystemMod.MODID);
    public static final RegistryObject<Item> BLOOD_ANALYZER_ITEM = ITEMS.register("blood_analyzer", () -> new BlockItem(ModBlocks.BLOOD_ANALYZER.get(), new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> MICROSCOPE_ITEM = ITEMS.register("microscope", () -> new BlockItem(ModBlocks.MICROSCOPE.get(), new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe", () -> new BloodSyringeItem(new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> BLOOD_BAG_AND_NEEDLE = ITEMS.register("blood_bag_and_needle", () -> new BloodBagAndNeedleItem(new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> BLOOD_BAG = ITEMS.register("blood_bag", () -> new BloodBagItem(new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> NEEDLE = ITEMS.register("needle", () -> new Item(new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
    public static final RegistryObject<Item> VEINAMITOL = ITEMS.register("veinamitol", () -> new BloodPillItem(new Item.Properties().tab(BloodSystemMod.BLOOD_SYSTEM_TAB)));
}
