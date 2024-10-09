package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.block.entity.BloodAnalyzerBlockEntity;
import me.Thelnfamous1.blood_system.common.block.entity.MicroscopeBlockEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BloodSystemMod.MODID);

    public static final RegistryObject<BlockEntityType<BloodAnalyzerBlockEntity>> BLOOD_ANALYZER = BLOCK_ENTITY_TYPES.register("blood_analyzer", () ->
            createBlockEntityType(BlockEntityType.Builder.of(BloodAnalyzerBlockEntity::new, ModBlocks.BLOOD_ANALYZER.get()), BloodSystemMod.location("blood_analyzer")));

    public static final RegistryObject<BlockEntityType<MicroscopeBlockEntity>> MICROSCOPE = BLOCK_ENTITY_TYPES.register("microscope", () ->
            createBlockEntityType(BlockEntityType.Builder.of(MicroscopeBlockEntity::new, ModBlocks.MICROSCOPE.get()), BloodSystemMod.location("microscope")));

    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityType.Builder<T> builder, ResourceLocation identifier) {
        return builder.build(Util.fetchChoiceType(References.BLOCK_ENTITY, identifier.toString()));
    }
}
