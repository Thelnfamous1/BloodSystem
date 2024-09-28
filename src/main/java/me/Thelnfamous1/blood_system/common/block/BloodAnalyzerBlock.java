package me.Thelnfamous1.blood_system.common.block;

import javax.annotation.Nullable;

import me.Thelnfamous1.blood_system.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BloodAnalyzerBlock extends AbstractBloodAnalyzerBlock {
   private static final VoxelShape BASE = makeShape();
   private static final VoxelShape NORTH_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.NORTH);
   private static final VoxelShape SOUTH_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.SOUTH);
   private static final VoxelShape EAST_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.EAST);
   private static final VoxelShape WEST_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.WEST);
   public BloodAnalyzerBlock(BlockBehaviour.Properties pProperties) {
      super(pProperties);
   }

   // Exported from BlockBench's VoxelShape Generators Plugin
   public static VoxelShape makeShape(){
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.3125, 0.75, 0.375, 0.6875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.625, 0.1875, 0.4375, 1, 0.3125, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.8125, 0.375, 0.4375, 0.9375, 0.5625, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.6875, 0.5625, 0.375, 1.0625, 0.875, 0.625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.28125, 0.6875, 0.1875, 0.3125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.28125, 0.375, 0.1875, 0.3125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.375, 0.6875, 0.39375, 0.625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.4375, 0.375, 0.3125, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.125, 0.3125, 0.4375, 0.25, 0.6875, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.0625, 0.5625, 0.4375, 0.4375, 0.6875, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.59375, 0.46875, 0.5625, 0.65625, 0.53125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.71875, 0.59375, 0.625, 1.03125, 0.84375, 0.65625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.28125, 0.03125, 0.6875, 0.71875, 0.34375, 0.71875), BooleanOp.OR);
      return shape;
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return /*new FurnaceBlockEntity(pPos, pState);*/ null;
   }

   @Override
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
       return switch (pState.getValue(AbstractBloodAnalyzerBlock.FACING)) {
           case NORTH -> NORTH_SHAPE;
           case SOUTH -> SOUTH_SHAPE;
           case EAST -> EAST_SHAPE;
           case WEST -> WEST_SHAPE;
           default -> BASE;
       };
   }

   @Override
   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return /*createFurnaceTicker(pLevel, pBlockEntityType, BlockEntityType.FURNACE);*/ null;
   }

   @Override
   protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
      /*
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof FurnaceBlockEntity) {
         pPlayer.openMenu((MenuProvider)blockentity);
         pPlayer.awardStat(Stats.INTERACT_WITH_FURNACE);
      }
       */

   }
}