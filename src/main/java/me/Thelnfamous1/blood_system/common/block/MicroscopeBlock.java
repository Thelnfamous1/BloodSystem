package me.Thelnfamous1.blood_system.common.block;

import me.Thelnfamous1.blood_system.common.block.entity.MicroscopeBlockEntity;
import me.Thelnfamous1.blood_system.common.registries.ModBlockEntityTypes;
import me.Thelnfamous1.blood_system.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class MicroscopeBlock extends AbstractBloodAnalyzerBlock {
   private static final VoxelShape BASE = makeShape();
   // Use opposite directions to get the Microscope's lens facing towards the desired direction
   private static final VoxelShape NORTH_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.SOUTH);
   private static final VoxelShape SOUTH_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.NORTH);
   private static final VoxelShape EAST_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.WEST);
   private static final VoxelShape WEST_SHAPE = VoxelShapeUtils.rotateHorizontal(BASE, Direction.EAST);

   public MicroscopeBlock(Properties pProperties) {
      super(pProperties);
   }

   // Exported from BlockBench's VoxelShape Generators Plugin
   public static VoxelShape makeShape(){
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.25, 0.6875, 0.25, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.5625, 0.625, 0.3125, 0.6875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.25, 0.625, 0.5625, 0.375, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.375, 0.6875, 0.5625, 0.625, 0.8125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.875, 0.6875, 0.5625, 0.9375, 0.8125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.375, 0.375, 0.625, 0.625, 0.875, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.28125, 0.125, 0.3125, 0.3125, 0.1875, 0.5), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.40625, 0.28125, 0.78125, 0.4375, 0.34375, 0.84375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.25, 0.6875, 0.5625, 0.375, 0.8125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.375, 0.25, 0.3125, 0.625, 0.26875, 0.5), BooleanOp.OR);

      return shape;
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new MicroscopeBlockEntity(pPos, pState);
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
      return createBloodAnalyzerTicker(pLevel, pBlockEntityType, ModBlockEntityTypes.MICROSCOPE.get());
   }

   @Override
   protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof MicroscopeBlockEntity microscopeBlockEntity) {
         pPlayer.openMenu(microscopeBlockEntity);
         //pPlayer.awardStat(Stats.INTERACT_WITH_FURNACE);
      }

   }
}