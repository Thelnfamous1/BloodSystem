package me.Thelnfamous1.blood_system.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.block.AbstractBloodAnalyzerBlock;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class AbstractBloodAnalyzerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
   public static final int SLOT_INPUT = 0;
   public static final int SLOT_BATTERY_A = 1;
   public static final int SLOT_BATTERY_B = 2;
   public static final int SLOT_RESULT = 3;
   public static final int MAX_CHARGE = 5;
   public static final int MIN_CHARGE_TO_START = 3;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{3, 2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1, 2};
   public static final int DATA_ACTIVATED = 0;
   public static final int DATA_CHARGE_A = 1;
   public static final int DATA_CHARGE_B = 2;
   public static final int DATA_ANALYSIS_PROGRESS = 3;
   public static final int DATA_ANALYSIS_TOTAL_TIME = 4;
   public static final int NUM_DATA_VALUES = 5;
   private final RecipeType<? extends BloodAnalysisRecipe> recipeType;
   protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   boolean activated;
   int chargeA;
   int chargeB;
   int analysisProgress;
   int analysisTotalTime;
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int id) {
          return switch (id) {
             case DATA_ACTIVATED -> AbstractBloodAnalyzerBlockEntity.this.activated ? 1 : 0;
             case DATA_CHARGE_A -> AbstractBloodAnalyzerBlockEntity.this.chargeA;
             case DATA_CHARGE_B -> AbstractBloodAnalyzerBlockEntity.this.chargeB;
             case DATA_ANALYSIS_PROGRESS -> AbstractBloodAnalyzerBlockEntity.this.analysisProgress;
             case DATA_ANALYSIS_TOTAL_TIME -> AbstractBloodAnalyzerBlockEntity.this.analysisTotalTime;
             default -> 0;
          };
      }

      @Override
      public void set(int id, int value) {
         switch (id) {
            case DATA_ACTIVATED:
               AbstractBloodAnalyzerBlockEntity.this.activated = value > 0;
               break;
            case DATA_CHARGE_A:
               AbstractBloodAnalyzerBlockEntity.this.chargeA = value;
               break;
            case DATA_CHARGE_B:
               AbstractBloodAnalyzerBlockEntity.this.chargeB = value;
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

   public static boolean isBattery(ItemStack pStack) {
      return BloodSystemConfig.SERVER.isBattery(pStack.getItem());
   }

   private boolean hasEnoughChargeToStartAnalysis() {
      return this.getTotalCharge() >= this.getMinimumChargeToStartAnalysis();
   }

   public boolean isAnalyzing() {
      return this.analysisProgress > 0;
   }

   public boolean isActivated(){
      return this.activated;
   }

   private boolean isFullyCharged(){
      return this.chargeA >= this.getMaxCharge(DATA_CHARGE_A) && this.chargeB >= this.getMaxCharge(DATA_CHARGE_B);
   }

   private boolean isCharged() {
      return this.chargeA > 0 || this.chargeB > 0;
   }

   private int getTotalCharge(){
      return this.chargeA + this.chargeB;
   }

   protected int getMinimumChargeToStartAnalysis() {
      return MIN_CHARGE_TO_START;
   }

   @Override
   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(pTag, this.items);
      this.activated = pTag.getBoolean("Activated");
      this.chargeA = pTag.getInt("ChargeA");
      this.chargeB = pTag.getInt("ChargeB");
      this.analysisProgress = pTag.getInt("AnalysisTime");
      this.analysisTotalTime = pTag.getInt("AnalysisTimeTotal");
      CompoundTag recipesUsed = pTag.getCompound("RecipesUsed");

      for(String id : recipesUsed.getAllKeys()) {
         this.recipesUsed.put(new ResourceLocation(id), recipesUsed.getInt(id));
      }

   }

   @Override
   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putBoolean("Activated", this.activated);
      pTag.putInt("ChargeA", this.chargeA);
      pTag.putInt("ChargeB", this.chargeB);
      pTag.putInt("AnalysisTime", this.analysisProgress);
      pTag.putInt("AnalysisTimeTotal", this.analysisTotalTime);
      ContainerHelper.saveAllItems(pTag, this.items);
      CompoundTag recipesUsed = new CompoundTag();
      this.recipesUsed.forEach((id, useCount) -> recipesUsed.putInt(id.toString(), useCount));
      pTag.put("RecipesUsed", recipesUsed);
   }

   public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, AbstractBloodAnalyzerBlockEntity pBlockEntity) {
      boolean wasCharged = pBlockEntity.isCharged();
      boolean changed = false;

      ItemStack batteryA = pBlockEntity.items.get(SLOT_BATTERY_A);
      ItemStack batteryB = pBlockEntity.items.get(SLOT_BATTERY_B);
      boolean hasInput = !pBlockEntity.items.get(SLOT_INPUT).isEmpty();
      boolean hasBattery = !batteryA.isEmpty() || !batteryB.isEmpty();
      if (pBlockEntity.isCharged() || hasBattery /*&& hasInput*/ || pBlockEntity.isAnalyzing()) {
         Recipe<?> recipe;
         if (hasInput) {
            recipe = pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).orElse(null);
         } else {
            recipe = null;
         }
         if(recipe == null){
            pBlockEntity.activated = false;
         }
         if(DebugFlags.DEBUG_BLOOD_ANALYZER && hasInput){
            BloodSystemMod.LOGGER.info("Found recipe {} for {}, has battery? {}", recipe == null ? null : recipe.getId(), pBlockEntity.items.get(SLOT_INPUT), hasBattery);
         }

         int maxStackSize = pBlockEntity.getMaxStackSize();
         if (!pBlockEntity.isFullyCharged()) {
            changed = charge(pBlockEntity, batteryA, batteryB);
         }

         boolean canAnalyze = pBlockEntity.canAnalyze(recipe, pBlockEntity.items, maxStackSize);
         boolean enoughCharge = pBlockEntity.hasEnoughChargeToStartAnalysis();
         if ((enoughCharge || pBlockEntity.isAnalyzing()) && canAnalyze) {
            if(pBlockEntity.activated){
               // Consume required charge before analyzing
               if(pBlockEntity.analysisProgress == 0){
                  int requiredChargeRemaining = pBlockEntity.getMinimumChargeToStartAnalysis();
                  int consumedChargeA = Math.min(pBlockEntity.chargeA, requiredChargeRemaining);
                  requiredChargeRemaining -= consumedChargeA;
                  pBlockEntity.chargeA -= consumedChargeA;
                  if(requiredChargeRemaining > 0){
                     int consumedChargeB = Math.min(pBlockEntity.chargeB, requiredChargeRemaining);
                     requiredChargeRemaining -= consumedChargeB;
                     pBlockEntity.chargeB -= consumedChargeB;
                  }
                  if(requiredChargeRemaining > 0){
                     if(!FMLEnvironment.production){
                        throw new IllegalStateException("Cannot have any charge remaining before beginning analysis!");
                     } else{
                        BloodSystemMod.LOGGER.error("{} still required {} charge for analysis, but began analysis anyway.", pBlockEntity, requiredChargeRemaining);
                     }
                  }
               }

               // Progress analysis
               ++pBlockEntity.analysisProgress;
               if (pBlockEntity.analysisProgress >= pBlockEntity.analysisTotalTime) {
                  pBlockEntity.analysisProgress = 0;
                  pBlockEntity.analysisTotalTime = getTotalAnalysisTime(pLevel, pBlockEntity);
                  if (pBlockEntity.analyze(recipe, pBlockEntity.items, maxStackSize)) {
                     pBlockEntity.setRecipeUsed(recipe);
                  }
                  if(pBlockEntity.analysisTotalTime <= 0){
                     pBlockEntity.activated = false;
                  }

                  changed = true;
               }
            }
         } else{
            if(!canAnalyze) pBlockEntity.analysisProgress = 0;
            pBlockEntity.activated = false;
         }
      }

      // started, charged -> on
      // started, uncharged -> off
      // not started, charged -> off
      // not started, uncharged -> off
      if(wasCharged != pBlockEntity.isCharged() || pState.getValue(AbstractBloodAnalyzerBlock.LIT) != pBlockEntity.activated) {
         changed = true;
         pState = pState.setValue(AbstractBloodAnalyzerBlock.LIT, pBlockEntity.activated && pBlockEntity.isCharged());
         pLevel.setBlock(pPos, pState, 3);
      }

      if (changed) {
         setChanged(pLevel, pPos, pState);
      }

   }

   private static boolean charge(AbstractBloodAnalyzerBlockEntity pBlockEntity, ItemStack batteryA, ItemStack batteryB) {
      int batteryACharge = pBlockEntity.getTotalCharge(batteryA);
      int batteryBCharge = pBlockEntity.getTotalCharge(batteryB);
      boolean chargedA = false;
      boolean chargedB = false;
      int maxChargeA = pBlockEntity.getMaxCharge(SLOT_BATTERY_A);
      if(pBlockEntity.chargeA < maxChargeA){
         chargedA = true;
         pBlockEntity.chargeA = Mth.clamp(pBlockEntity.chargeA + batteryACharge, 0, maxChargeA);
      }
      int maxChargeB = pBlockEntity.getMaxCharge(SLOT_BATTERY_B);
      if(pBlockEntity.chargeB < maxChargeB){
         chargedB = true;
         pBlockEntity.chargeB = Mth.clamp(pBlockEntity.chargeB + batteryBCharge, 0, maxChargeB);
      }
      if (pBlockEntity.isCharged()) {
         if (chargedA && batteryA.hasCraftingRemainingItem())
            pBlockEntity.items.set(SLOT_BATTERY_A, batteryA.getCraftingRemainingItem());
         if (chargedB && batteryB.hasCraftingRemainingItem())
            pBlockEntity.items.set(SLOT_BATTERY_B, batteryB.getCraftingRemainingItem());
         else {
            if (chargedA && !batteryA.isEmpty()) {
               batteryA.shrink(1);
               if (batteryA.isEmpty()) {
                  pBlockEntity.items.set(SLOT_BATTERY_A, batteryA.getCraftingRemainingItem());
               }
            }
            if (chargedB && !batteryB.isEmpty()) {
               batteryB.shrink(1);
               if (batteryB.isEmpty()) {
                  pBlockEntity.items.set(SLOT_BATTERY_B, batteryB.getCraftingRemainingItem());
               }
            }
         }
         return true;
      }
      return false;
   }

   protected int getMaxCharge(int slot) {
      return MAX_CHARGE;
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

   protected int getTotalCharge(ItemStack battery) {
      if (battery.isEmpty()) {
         return 0;
      } else {
         return BloodSystemConfig.SERVER.getBatteryCharge(battery.getItem());
      }
   }

   private static int getTotalAnalysisTime(Level pLevel, AbstractBloodAnalyzerBlockEntity pBlockEntity) {
      return pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).map(r -> pBlockEntity.getDefaultAnalysisTime()).orElse(0);
   }

   protected int getDefaultAnalysisTime(){
      return 100 /*2 * 60 * 20*/;
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
          return isBattery(pStack);
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