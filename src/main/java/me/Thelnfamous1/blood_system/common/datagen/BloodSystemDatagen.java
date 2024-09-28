package me.Thelnfamous1.blood_system.common.datagen;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.client.BloodSystemModClient;
import me.Thelnfamous1.blood_system.common.block.AbstractBloodAnalyzerBlock;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
import me.Thelnfamous1.blood_system.common.registries.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class BloodSystemDatagen {

    public static final ExistingFileHelper.ResourceType ITEM_TEXTURE_RESOURCE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures/item");
    public static final ExistingFileHelper.ResourceType BLOCK_TEXTURE_RESOURCE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures/block");

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new LanguageProvider(event.getGenerator(), BloodSystemMod.MODID, "en_us") {
            @Override
            protected void addTranslations() {
                this.add(ModAttributes.MAX_BLOOD.get().getDescriptionId(), "Max Blood");

                this.add(ModMobEffects.BLEEDING.get().getDescriptionId(), "Bleeding");
                this.add(ModMobEffects.CIRCULATION.get().getDescriptionId(), "Circulation");
                this.add(ModMobEffects.TRANSFUSION.get().getDescriptionId(), "Transfusion");

                this.add(BloodSystemMod.BLOOD_SYSTEM_TAB.getDisplayName().getString(), "Blood System");

                this.add(ModItems.VEINAMITOL.get().getDescriptionId(), "Veinamitol");
                this.add(ModItems.NEEDLE.get().getDescriptionId(), "Needle");
                this.add(ModItems.BLOOD_BAG.get().getDescriptionId(), "Blood Bag");
                this.add(ModItems.BLOOD_BAG_AND_NEEDLE.get().getDescriptionId(), "Blood Bag and Needle");
                this.add(ModItems.SYRINGE.get().getDescriptionId(), "Syringe");
                this.add(ModBlocks.BLOOD_ANALYZER.get().getDescriptionId(), "Blood Analyzer");

                this.add(BloodType.getCaption().getString(), "Blood Type");
                for(BloodType bloodType : BloodType.values()){
                    this.add(bloodType.getTranslationKey(), bloodType.getSerializedName());
                }
                this.add(BloodType.getUnknownDisplayName().getString(), "?");

                this.add(BloodSystemCommands.ERROR_MISSING_BLOOD_DATA.toString(), "%s is missing their Blood System data");
                this.add(BloodSystemCommands.GET_BLOOD_SUCCESS, "%s has %d blood");
                this.add(BloodSystemCommands.SET_BLOOD_SUCCESS, "Set the blood of %s to %d");
                this.add(BloodSystemCommands.GET_BLOOD_TYPE_SUCCESS, "%s has blood type %s");
                this.add(BloodSystemCommands.ERROR_NO_BLOOD_TYPE.toString(), "%s has no blood type");
                this.add(BloodSystemCommands.SET_BLOOD_TYPE_SUCCESS, "Set the blood type of %s to %s");
            }
        });
        event.getExistingFileHelper().trackGenerated(ModItems.VEINAMITOL.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(ModItems.NEEDLE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(ModItems.BLOOD_BAG.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(ModItems.BLOOD_BAG.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(ModItems.BLOOD_BAG_AND_NEEDLE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(ModItems.BLOOD_BAG_AND_NEEDLE.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(ModItems.SYRINGE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(ModItems.SYRINGE.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(ModBlocks.BLOOD_ANALYZER.getId(), BLOCK_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(ModBlocks.BLOOD_ANALYZER.getId().getPath() + "_on"), BLOCK_TEXTURE_RESOURCE);

        event.getGenerator().addProvider(event.includeClient(), new ItemModelProvider(event.getGenerator(), BloodSystemMod.MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerModels() {
                this.basicItem(ModItems.VEINAMITOL.get());
                this.basicItem(ModItems.NEEDLE.get());
                this.bloodFillableItem(ModItems.BLOOD_BAG.get());
                this.bloodFillableItem(ModItems.BLOOD_BAG_AND_NEEDLE.get());
                this.bloodFillableItem(ModItems.SYRINGE.get());
            }

            private void bloodFillableItem(Item item) {
                ItemModelBuilder emptySyringeModel = this.basicItem(item);
                ItemModelBuilder filledSyringeModel = this.alternateTextureItem(item, "_filled");
                emptySyringeModel
                        .override()
                        .predicate(BloodSystemModClient.BLOOD_FILLED_ITEM_PROPERTY, 1.0F)
                        .model(filledSyringeModel)
                        .end();
            }

            private ItemModelBuilder alternateTextureItem(Item item, String suffix)
            {
                return alternateTextureItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), suffix);
            }

            private ItemModelBuilder alternateTextureItem(ResourceLocation item, String suffix)
            {
                return getBuilder(item.toString() + suffix)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath() + suffix));
            }
        });

        event.getGenerator().addProvider(event.includeClient(), new BlockModelProvider(event.getGenerator(), BloodSystemMod.MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerModels() {
                ResourceLocation onModel = BloodSystemMod.location(ModBlocks.BLOOD_ANALYZER.getId().getPath() + "_on");
                ResourceLocation onTexture = new ResourceLocation(onModel.getNamespace(), "block/" + onModel.getPath());
                this.withExistingParent(onModel.toString(), ModBlocks.BLOOD_ANALYZER.getId())
                        .texture("0", onTexture)
                        .texture("particle", onTexture);
            }
        });

        event.getGenerator().addProvider(event.includeClient(), new BlockStateProvider(event.getGenerator(), BloodSystemMod.MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerStatesAndModels() {
                ModelFile.ExistingModelFile bloodAnalyzer = this.models().getExistingFile(ModBlocks.BLOOD_ANALYZER.getId());
                ModelFile.ExistingModelFile bloodAnalyzerOn = this.models().getExistingFile(BloodSystemMod.location(ModBlocks.BLOOD_ANALYZER.getId().getPath() + "_on"));
                this.horizontalBlock(ModBlocks.BLOOD_ANALYZER.get(), blockstate -> blockstate.getValue(AbstractBloodAnalyzerBlock.LIT) ? bloodAnalyzerOn : bloodAnalyzer);
            }
        });

        event.getGenerator().addProvider(event.includeServer(), new RecipeProvider(event.getGenerator()){
            @Override
            protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
                SpecialRecipeBuilder.special(ModRecipeSerializers.BLOOD_BAG_AND_NEEDLE.get()).save(pFinishedRecipeConsumer, BloodSystemMod.location("blood_bag_and_needle").toString());
            }
        });
    }
}
