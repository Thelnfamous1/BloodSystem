package me.Thelnfamous1.blood_system.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.block.AbstractBloodAnalyzerBlock;
import me.Thelnfamous1.blood_system.common.recipe.BloodAnalysisRecipe;
import me.Thelnfamous1.blood_system.common.util.DebugFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class AbstractBloodAnalyzerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
   public static final int SLOT_INPUT = 0;
   public static final int SLOT_BATTERY_A = 1;
   public static final int SLOT_BATTERY_B = 2;
   public static final int SLOT_RESULT = 3;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{3, 2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1, 2};
   public static final int DATA_CHARGE_TIME_A = 0;
   public static final int DATA_CHARGE_TIME_B = 1;
   public static final int DATA_CHARGE_DURATION_A = 2;
   public static final int DATA_CHARGE_DURATION_B = 3;
   public static final int DATA_ANALYSIS_PROGRESS = 4;
   public static final int DATA_ANALYSIS_TOTAL_TIME = 5;
   public static final int NUM_DATA_VALUES = 6;
   public static final int CHARGE_TIME_STANDARD = 200;
   public static final int CHARGE_CONSUME_SPEED = 2;
   private final RecipeType<? extends BloodAnalysisRecipe> recipeType;
   protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   int chargeTimeA;
   int chargeTimeB;
   int chargeDurationA;
   int chargeDurationB;
   int analysisProgress;
   int analysisTotalTime;
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int id) {
          return switch (id) {
             case DATA_CHARGE_TIME_A -> AbstractBloodAnalyzerBlockEntity.this.chargeTimeA;
             case DATA_CHARGE_TIME_B -> AbstractBloodAnalyzerBlockEntity.this.chargeTimeB;
             case DATA_CHARGE_DURATION_A -> AbstractBloodAnalyzerBlockEntity.this.chargeDurationA;
             case DATA_CHARGE_DURATION_B -> AbstractBloodAnalyzerBlockEntity.this.chargeDurationB;
             case DATA_ANALYSIS_PROGRESS -> AbstractBloodAnalyzerBlockEntity.this.analysisProgress;
             case DATA_ANALYSIS_TOTAL_TIME -> AbstractBloodAnalyzerBlockEntity.this.analysisTotalTime;
             default -> 0;
          };
      }

      @Override
      public void set(int id, int value) {
         switch (id) {
            case DATA_CHARGE_TIME_A:
               AbstractBloodAnalyzerBlockEntity.this.chargeTimeA = value;
               break;
            case DATA_CHARGE_TIME_B:
               AbstractBloodAnalyzerBlockEntity.this.chargeTimeB = value;
               break;
            case DATA_CHARGE_DURATION_A:
               AbstractBloodAnalyzerBlockEntity.this.chargeDurationA = value;
               break;
            case DATA_CHARGE_DURATION_B:
               AbstractBloodAnalyzerBlockEntity.this.chargeDurationB = value;
               break;
            case DATA_ANALYSIS_PROGRESS:
               AbstractBloodAnalyzerBlockEntity.this.analysisProgress = value;
               break;
            case DATA_ANALYSIS_TOTAL_TIME:
               AbstractBloodAnalyzerBlockEntity.this.analysisTotalTime = value;
         }

      }

      @Override
      public int getCount() {
         return NUM_DATA_VALUES;
      }
   };
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
   private final RecipeManager.CachedCheck<Container, ? extends BloodAnalysisRecipe> quickCheck;

   protected AbstractBloodAnalyzerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, RecipeType<? extends BloodAnalysisRecipe> pRecipeType) {
      super(pType, pPos, pBlockState);
      this.quickCheck = RecipeManager.createCheck((RecipeType)pRecipeType);
      this.recipeType = pRecipeType;
   }

   private boolean isLit() {
      return this.chargeTimeA > 0 || this.chargeTimeB > 0;
   }

   @Override
   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(pTag, this.items);
      this.chargeTimeA = pTag.getInt("ChargeTimeA");
      this.chargeTimeB = pTag.getInt("ChargeTimeB");
      this.analysisProgress = pTag.getInt("AnalysisTime");
      this.analysisTotalTime = pTag.getInt("AnalysisTimeTotal");
      this.chargeDurationA = this.getChargeDuration(this.items.get(SLOT_BATTERY_A));
      this.chargeDurationB = this.getChargeDuration(this.items.get(SLOT_BATTERY_B));
      CompoundTag recipesUsed = pTag.getCompound("RecipesUsed");

      for(String id : recipesUsed.getAllKeys()) {
         this.recipesUsed.put(new ResourceLocation(id), recipesUsed.getInt(id));
      }

   }

   @Override
   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putInt("ChargeTimeA", this.chargeTimeA);
      pTag.putInt("ChargeTimeB", this.chargeTimeB);
      pTag.putInt("AnalysisTime", this.analysisProgress);
      pTag.putInt("AnalysisTimeTotal", this.analysisTotalTime);
      ContainerHelper.saveAllItems(pTag, this.items);
      CompoundTag recipesUsed = new CompoundTag();
      this.recipesUsed.forEach((id, useCount) -> recipesUsed.putInt(id.toString(), useCount));
      pTag.put("RecipesUsed", recipesUsed);
   }

   public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, AbstractBloodAnalyzerBlockEntity pBlockEntity) {
      boolean wasLit = pBlockEntity.isLit();
      boolean changed = false;
      if (pBlockEntity.isLit()) {
         if(pBlockEntity.chargeTimeA > 0){
            --pBlockEntity.chargeTimeA;
         } else if(pBlockEntity.chargeTimeB > 0){
            --pBlockEntity.chargeTimeB;
         }
      }

      ItemStack batteryA = pBlockEntity.items.get(SLOT_BATTERY_A);
      ItemStack batteryB = pBlockEntity.items.get(SLOT_BATTERY_B);
      boolean hasInput = !pBlockEntity.items.get(SLOT_INPUT).isEmpty();
      boolean hasBattery = !batteryA.isEmpty() || !batteryB.isEmpty();
      if (pBlockEntity.isLit() || hasBattery && hasInput) {
         Recipe<?> recipe;
         if (hasInput) {
            recipe = pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).orElse(null);
         } else {
            recipe = null;
         }
         if(DebugFlags.DEBUG_BLOOD_ANALYZER && hasInput){
            BloodSystemMod.LOGGER.info("Found recipe {} for {}, has charge? {}", recipe == null ? null : recipe.getId(), pBlockEntity.items.get(SLOT_INPUT), hasBattery);
         }

         int maxStackSize = pBlockEntity.getMaxStackSize();
         if (!pBlockEntity.isLit() && pBlockEntity.canAnalyze(recipe, pBlockEntity.items, maxStackSize)) {
            changed = consumeChargeFromBattery(pBlockEntity, batteryA, batteryB);
         }

         if (pBlockEntity.isLit() && pBlockEntity.canAnalyze(recipe, pBlockEntity.items, maxStackSize)) {
            ++pBlockEntity.analysisProgress;
            if (pBlockEntity.analysisProgress == pBlockEntity.analysisTotalTime) {
               pBlockEntity.analysisProgress = 0;
               pBlockEntity.analysisTotalTime = getTotalAnalysisTime(pLevel, pBlockEntity);
               if (pBlockEntity.analyze(recipe, pBlockEntity.items, maxStackSize)) {
                  pBlockEntity.setRecipeUsed(recipe);
               }

               changed = true;
            }
         } else {
            pBlockEntity.analysisProgress = 0;
         }
      } else if (!pBlockEntity.isLit() && pBlockEntity.analysisProgress > 0) {
         pBlockEntity.analysisProgress = Mth.clamp(pBlockEntity.analysisProgress - CHARGE_CONSUME_SPEED, 0, pBlockEntity.analysisTotalTime);
      }

      if (wasLit != pBlockEntity.isLit()) {
         changed = true;
         pState = pState.setValue(AbstractBloodAnalyzerBlock.LIT, pBlockEntity.isLit());
         pLevel.setBlock(pPos, pState, 3);
      }

      if (changed) {
         setChanged(pLevel, pPos, pState);
      }

   }

   private static boolean consumeChargeFromBattery(AbstractBloodAnalyzerBlockEntity pBlockEntity, ItemStack batteryA, ItemStack batteryB) {
      int batteryACharge = pBlockEntity.getChargeDuration(batteryA);
      int batteryBCharge = pBlockEntity.getChargeDuration(batteryB);
      int slotUsed;
      ItemStack batteryUsed;
      if(batteryACharge >= batteryBCharge){
         slotUsed = SLOT_BATTERY_A;
         batteryUsed = batteryA;
         pBlockEntity.chargeTimeA = batteryACharge;
         pBlockEntity.chargeDurationA = pBlockEntity.chargeTimeA;
      } else{
         slotUsed = SLOT_BATTERY_B;
         batteryUsed = batteryB;
         pBlockEntity.chargeTimeB = batteryBCharge;
         pBlockEntity.chargeDurationB = pBlockEntity.chargeTimeB;
      }
      if (pBlockEntity.isLit()) {
         if (batteryUsed.hasCraftingRemainingItem())
            pBlockEntity.items.set(slotUsed, batteryUsed.getCraftingRemainingItem());
         else
         if (!batteryUsed.isEmpty()) {
            batteryUsed.shrink(1);
            if (batteryUsed.isEmpty()) {
               pBlockEntity.items.set(slotUsed, batteryUsed.getCraftingRemainingItem());
            }
         }
         return true;
      }
      return false;
   }

   private boolean canAnalyze(@Nullable Recipe<?> pRecipe, NonNullList<ItemStack> pStacks, int pStackSize) {
      if (!pStacks.get(SLOT_INPUT).isEmpty() && pRecipe != null) {
         ItemStack assemble = ((Recipe<WorldlyContainer>) pRecipe).assemble(this);
         if (assemble.isEmpty()) {
            return false;
         } else {
            ItemStack result = pStacks.get(SLOT_RESULT);
            if (result.isEmpty()) {
               return true;
            } else if (!result.sameItem(assemble)) {
               return false;
            } else if (result.getCount() + assemble.getCount() <= pStackSize && result.getCount() + assemble.getCount() <= result.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
               return true;
            } else {
               return result.getCount() + assemble.getCount() <= assemble.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
            }
         }
      } else {
         return false;
      }
   }

   private boolean analyze(@Nullable Recipe<?> pRecipe, NonNullList<ItemStack> pStacks, int pStackSize) {
      if (pRecipe != null && this.canAnalyze(pRecipe, pStacks, pStackSize)) {
         ItemStack input = pStacks.get(SLOT_INPUT);
         ItemStack assemble = ((Recipe<WorldlyContainer>) pRecipe).assemble(this);
         ItemStack result = pStacks.get(SLOT_RESULT);
         if (result.isEmpty()) {
            pStacks.set(SLOT_RESULT, assemble.copy());
         } else if (result.is(assemble.getItem())) {
            result.grow(assemble.getCount());
         }

         input.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   protected int getChargeDuration(ItemStack pFuel) {
      if (pFuel.isEmpty()) {
         return 0;
      } else {
         return ForgeHooks.getBurnTime(pFuel, this.recipeType);
      }
   }

   private static int getTotalAnalysisTime(Level pLevel, AbstractBloodAnalyzerBlockEntity pBlockEntity) {
      return pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).map(/*BloodAnalysisRecipe::getCookingTime*/r -> CHARGE_TIME_STANDARD).orElse(CHARGE_TIME_STANDARD);
   }

   public static boolean isFuel(ItemStack pStack) {
      return ForgeHooks.getBurnTime(pStack, null) > 0;
   }

   @Override
   public int[] getSlotsForFace(Direction pSide) {
      if (pSide == Direction.DOWN) {
         return SLOTS_FOR_DOWN;
      } else {
         return pSide == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
      }
   }

   /**
    * Returns {@code true} if automation can insert the given item in the given slot from the given side.
    */
   @Override
   public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
      return this.canPlaceItem(pIndex, pItemStack);
   }

   /**
    * Returns {@code true} if automation can extract the given item in the given slot from the given side.
    */
   @Override
   public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
       return pDirection != Direction.DOWN || (pIndex != SLOT_BATTERY_A && pIndex != SLOT_BATTERY_B);
   }

   /**
    * Returns the number of slots in the inventory.
    */
   @Override
   public int getContainerSize() {
      return this.items.size();
   }

   @Override
   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   @Override
   public ItemStack getItem(int pIndex) {
      return this.items.get(pIndex);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   @Override
   public ItemStack removeItem(int pIndex, int pCount) {
      return ContainerHelper.removeItem(this.items, pIndex, pCount);
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   @Override
   public ItemStack removeItemNoUpdate(int pIndex) {
      return ContainerHelper.takeItem(this.items, pIndex);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   @Override
   public void setItem(int pIndex, ItemStack pStack) {
      ItemStack itemInSlot = this.items.get(pIndex);
      boolean matches = !pStack.isEmpty() && pStack.sameItem(itemInSlot) && ItemStack.tagMatches(pStack, itemInSlot);
      this.items.set(pIndex, pStack);
      if (pStack.getCount() > this.getMaxStackSize()) {
         pStack.setCount(this.getMaxStackSize());
      }

      if (pIndex == SLOT_INPUT && !matches) {
         this.analysisTotalTime = getTotalAnalysisTime(this.level, this);
         this.analysisProgress = 0;
         this.setChanged();
      }

   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   @Override
   public boolean stillValid(Player pPlayer) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
      }
   }

   /**
    * Returns {@code true} if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
    * For guis use Slot.isItemValid
    */
   @Override
   public boolean canPlaceItem(int pIndex, ItemStack pStack) {
      if (pIndex == SLOT_RESULT) {
         return false;
      } else if (pIndex != SLOT_BATTERY_A && pIndex != SLOT_BATTERY_B) {
         return true;
      } else {
          return ForgeHooks.getBurnTime(pStack, this.recipeType) > 0;
      }
   }

   @Override
   public void clearContent() {
      this.items.clear();
   }

   @Override
   public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {
      if (pRecipe != null) {
         ResourceLocation resourcelocation = pRecipe.getId();
         this.recipesUsed.addTo(resourcelocation, 1);
      }

   }

   @Override
   @Nullable
   public Recipe<?> getRecipeUsed() {
      return null;
   }

   @Override
   public void awardUsedRecipes(Player pPlayer) {
   }

   public void awardUsedRecipesAndPopExperience(ServerPlayer pPlayer) {
      List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(pPlayer.getLevel(), pPlayer.position());
      pPlayer.awardRecipes(list);
      this.recipesUsed.clear();
   }

   public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec) {
      List<Recipe<?>> recipes = Lists.newArrayList();

      for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
         pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
            recipes.add(recipe);
            createExperience(pLevel, pPopVec, entry.getIntValue(), /*((BloodAnalysisRecipe)recipe).getExperience()*/1);
         });
      }

      return recipes;
   }

   private static void createExperience(ServerLevel pLevel, Vec3 pPopVec, int pRecipeIndex, float pExperience) {
      int i = Mth.floor((float)pRecipeIndex * pExperience);
      float f = Mth.frac((float)pRecipeIndex * pExperience);
      if (f != 0.0F && Math.random() < (double)f) {
         ++i;
      }

      ExperienceOrb.award(pLevel, pPopVec, i);
   }

   @Override
   public void fillStackedContents(StackedContents pHelper) {
      for(ItemStack itemstack : this.items) {
         pHelper.accountStack(itemstack);
      }

   }

   LazyOptional<? extends IItemHandler>[] handlers =
           SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

   @Override
   public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
      if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
         if (facing == Direction.UP)
            return handlers[0].cast();
         else if (facing == Direction.DOWN)
            return handlers[1].cast();
         else
            return handlers[2].cast();
      }
      return super.getCapability(capability, facing);
   }

   @Override
   public void invalidateCaps() {
      super.invalidateCaps();
       for (LazyOptional<? extends IItemHandler> handler : handlers)
           handler.invalidate();
   }

   @Override
   public void reviveCaps() {
      super.reviveCaps();
      this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
   }
}