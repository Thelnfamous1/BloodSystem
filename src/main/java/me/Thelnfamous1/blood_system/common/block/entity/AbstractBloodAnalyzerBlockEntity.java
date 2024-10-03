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
   public static final int SLOT_FUEL = 1;
   public static final int SLOT_RESULT = 2;
   public static final int DATA_LIT_TIME = 0;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   public static final int DATA_LIT_DURATION = 1;
   public static final int DATA_COOKING_PROGRESS = 2;
   public static final int DATA_COOKING_TOTAL_TIME = 3;
   public static final int NUM_DATA_VALUES = 4;
   public static final int BURN_TIME_STANDARD = 200;
   public static final int BURN_COOL_SPEED = 2;
   private final RecipeType<? extends BloodAnalysisRecipe> recipeType;
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   int litTime;
   int litDuration;
   int cookingProgress;
   int cookingTotalTime;
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int id) {
          return switch (id) {
              case DATA_LIT_TIME -> AbstractBloodAnalyzerBlockEntity.this.litTime;
              case DATA_LIT_DURATION -> AbstractBloodAnalyzerBlockEntity.this.litDuration;
              case DATA_COOKING_PROGRESS -> AbstractBloodAnalyzerBlockEntity.this.cookingProgress;
              case DATA_COOKING_TOTAL_TIME -> AbstractBloodAnalyzerBlockEntity.this.cookingTotalTime;
              default -> 0;
          };
      }

      @Override
      public void set(int id, int value) {
         switch (id) {
            case DATA_LIT_TIME:
               AbstractBloodAnalyzerBlockEntity.this.litTime = value;
               break;
            case DATA_LIT_DURATION:
               AbstractBloodAnalyzerBlockEntity.this.litDuration = value;
               break;
            case DATA_COOKING_PROGRESS:
               AbstractBloodAnalyzerBlockEntity.this.cookingProgress = value;
               break;
            case DATA_COOKING_TOTAL_TIME:
               AbstractBloodAnalyzerBlockEntity.this.cookingTotalTime = value;
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
      return this.litTime > 0;
   }

   @Override
   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(pTag, this.items);
      this.litTime = pTag.getInt("BurnTime");
      this.cookingProgress = pTag.getInt("CookTime");
      this.cookingTotalTime = pTag.getInt("CookTimeTotal");
      this.litDuration = this.getBurnDuration(this.items.get(SLOT_FUEL));
      CompoundTag recipesUsed = pTag.getCompound("RecipesUsed");

      for(String id : recipesUsed.getAllKeys()) {
         this.recipesUsed.put(new ResourceLocation(id), recipesUsed.getInt(id));
      }

   }

   @Override
   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putInt("BurnTime", this.litTime);
      pTag.putInt("CookTime", this.cookingProgress);
      pTag.putInt("CookTimeTotal", this.cookingTotalTime);
      ContainerHelper.saveAllItems(pTag, this.items);
      CompoundTag recipesUsed = new CompoundTag();
      this.recipesUsed.forEach((id, useCount) -> recipesUsed.putInt(id.toString(), useCount));
      pTag.put("RecipesUsed", recipesUsed);
   }

   public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, AbstractBloodAnalyzerBlockEntity pBlockEntity) {
      boolean wasLit = pBlockEntity.isLit();
      boolean changed = false;
      if (pBlockEntity.isLit()) {
         --pBlockEntity.litTime;
      }

      ItemStack fuel = pBlockEntity.items.get(SLOT_FUEL);
      boolean hasInput = !pBlockEntity.items.get(SLOT_INPUT).isEmpty();
      boolean hasFuel = !fuel.isEmpty();
      if (pBlockEntity.isLit() || hasFuel && hasInput) {
         Recipe<?> recipe;
         if (hasInput) {
            recipe = pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).orElse(null);
         } else {
            recipe = null;
         }
         if(DebugFlags.DEBUG_BLOOD_ANALYZER && hasInput){
            BloodSystemMod.LOGGER.info("Found recipe {} for {}, has fuel? {}", recipe == null ? null : recipe.getId(), pBlockEntity.items.get(SLOT_INPUT), hasFuel);
         }

         int maxStackSize = pBlockEntity.getMaxStackSize();
         if (!pBlockEntity.isLit() && pBlockEntity.canBurn(recipe, pBlockEntity.items, maxStackSize)) {
            pBlockEntity.litTime = pBlockEntity.getBurnDuration(fuel);
            pBlockEntity.litDuration = pBlockEntity.litTime;
            if (pBlockEntity.isLit()) {
               changed = true;
               if (fuel.hasCraftingRemainingItem())
                  pBlockEntity.items.set(SLOT_FUEL, fuel.getCraftingRemainingItem());
               else
               if (hasFuel) {
                  fuel.shrink(1);
                  if (fuel.isEmpty()) {
                     pBlockEntity.items.set(SLOT_FUEL, fuel.getCraftingRemainingItem());
                  }
               }
            }
         }

         if (pBlockEntity.isLit() && pBlockEntity.canBurn(recipe, pBlockEntity.items, maxStackSize)) {
            ++pBlockEntity.cookingProgress;
            if (pBlockEntity.cookingProgress == pBlockEntity.cookingTotalTime) {
               pBlockEntity.cookingProgress = 0;
               pBlockEntity.cookingTotalTime = getTotalCookTime(pLevel, pBlockEntity);
               if (pBlockEntity.burn(recipe, pBlockEntity.items, maxStackSize)) {
                  pBlockEntity.setRecipeUsed(recipe);
               }

               changed = true;
            }
         } else {
            pBlockEntity.cookingProgress = 0;
         }
      } else if (!pBlockEntity.isLit() && pBlockEntity.cookingProgress > 0) {
         pBlockEntity.cookingProgress = Mth.clamp(pBlockEntity.cookingProgress - BURN_COOL_SPEED, 0, pBlockEntity.cookingTotalTime);
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

   private boolean canBurn(@Nullable Recipe<?> pRecipe, NonNullList<ItemStack> pStacks, int pStackSize) {
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

   private boolean burn(@Nullable Recipe<?> pRecipe, NonNullList<ItemStack> pStacks, int pStackSize) {
      if (pRecipe != null && this.canBurn(pRecipe, pStacks, pStackSize)) {
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

   protected int getBurnDuration(ItemStack pFuel) {
      if (pFuel.isEmpty()) {
         return 0;
      } else {
         return ForgeHooks.getBurnTime(pFuel, this.recipeType);
      }
   }

   private static int getTotalCookTime(Level pLevel, AbstractBloodAnalyzerBlockEntity pBlockEntity) {
      return pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).map(/*BloodAnalysisRecipe::getCookingTime*/r -> BURN_TIME_STANDARD).orElse(BURN_TIME_STANDARD);
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
       return pDirection != Direction.DOWN || pIndex != SLOT_FUEL;
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
         this.cookingTotalTime = getTotalCookTime(this.level, this);
         this.cookingProgress = 0;
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
      } else if (pIndex != SLOT_FUEL) {
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