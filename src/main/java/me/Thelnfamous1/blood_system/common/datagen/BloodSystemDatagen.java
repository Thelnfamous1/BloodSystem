package me.Thelnfamous1.blood_system.common.datagen;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.client.BloodSystemModClient;
import me.Thelnfamous1.blood_system.common.block.AbstractBloodAnalyzerBlock;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
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

public class BloodSystemDatagen {

    public static final ExistingFileHelper.ResourceType ITEM_TEXTURE_RESOURCE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures/item");
    public static final ExistingFileHelper.ResourceType BLOCK_TEXTURE_RESOURCE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures/block");

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new LanguageProvider(event.getGenerator(), BloodSystemMod.MODID, "en_us") {
            @Override
            protected void addTranslations() {
                this.add(BloodSystemMod.MAX_BLOOD.get().getDescriptionId(), "Max Blood");

                this.add(BloodSystemMod.BLEEDING.get().getDescriptionId(), "Bleeding");
                this.add(BloodSystemMod.CIRCULATION.get().getDescriptionId(), "Circulation");
                this.add(BloodSystemMod.TRANSFUSION.get().getDescriptionId(), "Transfusion");

                this.add(BloodSystemMod.BLOOD_SYSTEM_TAB.getDisplayName().getString(), "Blood System");

                this.add(BloodSystemMod.VEINAMITOL.get().getDescriptionId(), "Veinamitol");
                this.add(BloodSystemMod.NEEDLE.get().getDescriptionId(), "Needle");
                this.add(BloodSystemMod.BLOOD_BAG.get().getDescriptionId(), "Blood Bag");
                this.add(BloodSystemMod.BLOOD_BAG_AND_NEEDLE.get().getDescriptionId(), "Blood Bag and Needle");
                this.add(BloodSystemMod.SYRINGE.get().getDescriptionId(), "Syringe");
                this.add(BloodSystemMod.BLOOD_ANALYZER.get().getDescriptionId(), "Blood Analyzer");

                this.add(BloodType.getCaption().getString(), "Blood Type");
                for(BloodType bloodType : BloodType.values()){
                    this.add(bloodType.getTranslationKey(), bloodType.getSerializedName());
                }

                this.add(BloodSystemCommands.ERROR_MISSING_BLOOD_DATA.toString(), "%s is missing their Blood System data");
                this.add(BloodSystemCommands.GET_BLOOD_SUCCESS, "%s has %d blood");
                this.add(BloodSystemCommands.SET_BLOOD_SUCCESS, "Set the blood of %s to %d");
                this.add(BloodSystemCommands.GET_BLOOD_TYPE_SUCCESS, "%s has blood type %s");
                this.add(BloodSystemCommands.ERROR_NO_BLOOD_TYPE.toString(), "%s has no blood type");
                this.add(BloodSystemCommands.SET_BLOOD_TYPE_SUCCESS, "Set the blood type of %s to %s");
            }
        });
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.VEINAMITOL.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.NEEDLE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.BLOOD_BAG.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(BloodSystemMod.BLOOD_BAG.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.BLOOD_BAG_AND_NEEDLE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(BloodSystemMod.BLOOD_BAG_AND_NEEDLE.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.SYRINGE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(BloodSystemMod.SYRINGE.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.BLOOD_ANALYZER.getId(), BLOCK_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(BloodSystemMod.BLOOD_ANALYZER.getId().getPath() + "_on"), BLOCK_TEXTURE_RESOURCE);

        event.getGenerator().addProvider(event.includeClient(), new ItemModelProvider(event.getGenerator(), BloodSystemMod.MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerModels() {
                this.basicItem(BloodSystemMod.VEINAMITOL.get());
                this.basicItem(BloodSystemMod.NEEDLE.get());
                this.bloodFillableItem(BloodSystemMod.BLOOD_BAG.get());
                this.bloodFillableItem(BloodSystemMod.BLOOD_BAG_AND_NEEDLE.get());
                this.bloodFillableItem(BloodSystemMod.SYRINGE.get());
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
                ResourceLocation onModel = BloodSystemMod.location(BloodSystemMod.BLOOD_ANALYZER.getId().getPath() + "_on");
                ResourceLocation onTexture = new ResourceLocation(onModel.getNamespace(), "block/" + onModel.getPath());
                this.withExistingParent(onModel.toString(), BloodSystemMod.BLOOD_ANALYZER.getId())
                        .texture("0", onTexture)
                        .texture("particle", onTexture);
            }
        });

        event.getGenerator().addProvider(event.includeClient(), new BlockStateProvider(event.getGenerator(), BloodSystemMod.MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerStatesAndModels() {
                ModelFile.ExistingModelFile bloodAnalyzer = this.models().getExistingFile(BloodSystemMod.BLOOD_ANALYZER.getId());
                ModelFile.ExistingModelFile bloodAnalyzerOn = this.models().getExistingFile(BloodSystemMod.location(BloodSystemMod.BLOOD_ANALYZER.getId().getPath() + "_on"));
                this.horizontalBlock(BloodSystemMod.BLOOD_ANALYZER.get(), blockstate -> blockstate.getValue(AbstractBloodAnalyzerBlock.LIT) ? bloodAnalyzerOn : bloodAnalyzer);
            }
        });
    }
}
