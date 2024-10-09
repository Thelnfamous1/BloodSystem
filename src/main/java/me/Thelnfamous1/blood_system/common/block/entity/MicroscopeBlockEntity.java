package me.Thelnfamous1.blood_system.common.block.entity;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.menu.AbstractBloodAnalyzerMenu;
import me.Thelnfamous1.blood_system.common.menu.MicroscopeMenu;
import me.Thelnfamous1.blood_system.common.registries.ModBlockEntityTypes;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeTypes;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class MicroscopeBlockEntity extends AbstractBloodAnalyzerBlockEntity {
   public static final String NAME_KEY = BloodSystemMod.translationKey("container", "microscope");
   public static final String START_BUTTON_KEY = BloodSystemMod.translationKey("container", "microscope.start");
   private static final int DEFAULT_ANALYSIS_TIME = 3 * 60 * 20;
   public static final int MIN_CHARGE_TO_START = 2;

   public MicroscopeBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super(ModBlockEntityTypes.MICROSCOPE.get(), pPos, pBlockState, ModRecipeTypes.BLOOD_ANALYSIS.get());
   }

   @Override
   protected Component getDefaultName() {
      return Component.translatable(NAME_KEY);
   }

   @Override
   protected AbstractBloodAnalyzerMenu createMenu(int pId, Inventory pPlayer) {
      return new MicroscopeMenu(pId, pPlayer, this, this.dataAccess);
   }

   @Override
   protected int getDefaultAnalysisTime(){
      if(DebugFlags.DEBUG_MICROSCOPE)
         return 300;
      return DEFAULT_ANALYSIS_TIME;
   }

   @Override
   protected int getMinimumChargeToStartAnalysis() {
      return MIN_CHARGE_TO_START;
   }

   @Override
   public void stopOpen(Player pPlayer) {
      this.activated = false;
   }
}