package me.Thelnfamous1.blood_system.common.block;

import me.Thelnfamous1.blood_system.common.item.BloodFillableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractBloodAnalyzerBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;

   protected AbstractBloodAnalyzerBlock(BlockBehaviour.Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE));
   }

   @Override
   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      ItemStack itemInHand = pPlayer.getItemInHand(pHand);
      if(itemInHand.getItem() instanceof BloodFillableItem && BloodFillableItem.getStoredBloodType(itemInHand).isPresent() && !BloodFillableItem.isAnalyzed(itemInHand)){
         pPlayer.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
         if(!pLevel.isClientSide){
            if(pPlayer.isSecondaryUseActive()){
               BloodFillableItem.setAnalyzed(itemInHand, true);
            } else{
               BloodFillableItem.createAndGiveAnalyzedContainer(itemInHand, pPlayer);
            }
         }
         return InteractionResult.sidedSuccess(pLevel.isClientSide);
      }
      return InteractionResult.PASS;
      /*
      if (pLevel.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         this.openContainer(pLevel, pPos, pPlayer);
         return InteractionResult.CONSUME;
      }
       */
   }

   protected abstract void openContainer(Level pLevel, BlockPos pPos, Player pPlayer);

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
   }

   @Override
   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
      /*
      if (pStack.hasCustomHoverName()) {
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof AbstractFurnaceBlockEntity) {
            ((AbstractFurnaceBlockEntity)blockentity).setCustomName(pStack.getHoverName());
         }
      }
      */

   }

   @Override
   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         /*
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof AbstractFurnaceBlockEntity) {
            if (pLevel instanceof ServerLevel) {
               Containers.dropContents(pLevel, pPos, (AbstractFurnaceBlockEntity)blockentity);
               ((AbstractFurnaceBlockEntity)blockentity).getRecipesToAwardAndPopExperience((ServerLevel)pLevel, Vec3.atCenterOf(pPos));
            }

            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }
         */

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState pState) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
   }

   @Override
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @Override
   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
      pBuilder.add(FACING, LIT);
   }

   /*
   @Nullable
   protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(Level pLevel, BlockEntityType<T> pServerType, BlockEntityType<? extends AbstractFurnaceBlockEntity> pClientType) {
      return pLevel.isClientSide ? null : createTickerHelper(pServerType, pClientType, AbstractFurnaceBlockEntity::serverTick);
   }
    */
}