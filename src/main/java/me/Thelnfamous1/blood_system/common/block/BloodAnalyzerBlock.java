package me.Thelnfamous1.blood_system.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BloodAnalyzerBlock extends AbstractBloodAnalyzerBlock {
   public BloodAnalyzerBlock(BlockBehaviour.Properties pProperties) {
      super(pProperties);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return /*new FurnaceBlockEntity(pPos, pState);*/ null;
   }

   @Override
   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return /*createFurnaceTicker(pLevel, pBlockEntityType, BlockEntityType.FURNACE);*/ null;
   }

   @Override
   protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      /*
      if (blockentity instanceof FurnaceBlockEntity) {
         pPlayer.openMenu((MenuProvider)blockentity);
         pPlayer.awardStat(Stats.INTERACT_WITH_FURNACE);
      }
       */

   }
}