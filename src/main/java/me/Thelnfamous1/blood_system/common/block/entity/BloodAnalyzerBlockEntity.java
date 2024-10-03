package me.Thelnfamous1.blood_system.common.block.entity;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.menu.AbstractBloodAnalyzerMenu;
import me.Thelnfamous1.blood_system.common.menu.BloodAnalyzerMenu;
import me.Thelnfamous1.blood_system.common.registries.ModBlockEntityTypes;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class BloodAnalyzerBlockEntity extends AbstractBloodAnalyzerBlockEntity {
   public static final String NAME_KEY = BloodSystemMod.translationKey("container", "blood_analyzer");

   public BloodAnalyzerBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super(ModBlockEntityTypes.BLOOD_ANALYZER.get(), pPos, pBlockState, ModRecipeTypes.BLOOD_ANALYSIS.get());
   }

   @Override
   protected Component getDefaultName() {
      return Component.translatable(NAME_KEY);
   }

   @Override
   protected AbstractBloodAnalyzerMenu createMenu(int pId, Inventory pPlayer) {
      return new BloodAnalyzerMenu(pId, pPlayer, this, this.dataAccess);
   }
}