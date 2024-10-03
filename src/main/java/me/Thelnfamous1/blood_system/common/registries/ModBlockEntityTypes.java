package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.block.entity.BloodAnalyzerBlockEntity;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BloodSystemMod.MODID);

    public static final RegistryObject<BlockEntityType<BloodAnalyzerBlockEntity>> BLOOD_ANALYZER = BLOCK_ENTITY_TYPES.register("blood_analyzer", () -> createBlockEntityType(BlockEntityType.Builder.of(BloodAnalyzerBlockEntity::new, ModBlocks.BLOOD_ANALYZER.get())));

    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityType.Builder<T> builder) {
        return builder.build(Util.fetchChoiceType(References.BLOCK_ENTITY, BloodSystemMod.location("blood_analyzer").toString()));
    }
}
