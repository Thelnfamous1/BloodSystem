package me.Thelnfamous1.blood_system.common.block.entity;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.menu.AbstractBloodAnalyzerMenu;
import me.Thelnfamous1.blood_system.common.menu.BloodAnalyzerMenu;
import me.Thelnfamous1.blood_system.common.registries.ModBlockEntityTypes;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeTypes;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class BloodAnalyzerBlockEntity extends AbstractBloodAnalyzerBlockEntity {
   public static final String NAME_KEY = BloodSystemMod.translationKey("container", "blood_analyzer");
   public static final String START_BUTTON_KEY = BloodSystemMod.translationKey("container", "blood_analyzer.start");
   private static final int DEFAULT_ANALYSIS_TIME = 2 * 60 * 20;
   public static final int MIN_CHARGE_TO_START = 3;

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

    @Override
    protected int getDefaultAnalysisTime(){
       if(DebugFlags.DEBUG_BLOOD_ANALYZER)
          return 200;
       return DEFAULT_ANALYSIS_TIME;
    }

   @Override
   protected int getMinimumChargeToStartAnalysis() {
      return MIN_CHARGE_TO_START;
   }
}