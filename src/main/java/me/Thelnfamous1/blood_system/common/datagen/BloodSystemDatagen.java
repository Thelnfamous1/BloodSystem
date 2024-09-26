package me.Thelnfamous1.blood_system.common.datagen;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.client.BloodSystemModClient;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class BloodSystemDatagen {

    public static final ExistingFileHelper.ResourceType ITEM_TEXTURE_RESOURCE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures/item");

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
                this.add(BloodSystemMod.BLOOD_BAG.get().getDescriptionId(), "Blood Bag");
                this.add(BloodSystemMod.SYRINGE.get().getDescriptionId(), "Syringe");
                for(BloodType bloodType : BloodType.values()){
                    this.add(bloodType.getTranslationKey(), bloodType.getSerializedName());
                }
                this.add(BloodSystemCommands.GET_BLOOD_SUCCESS, "%s has %d blood");
                this.add(BloodSystemCommands.SET_BLOOD_SUCCESS, "Set the blood of %s to %d");
                this.add(BloodSystemCommands.GET_BLOOD_TYPE_SUCCESS, "%s has blood type %s");
                this.add(BloodSystemCommands.SET_BLOOD_TYPE_SUCCESS, "Set the blood type of %s to %s");
            }
        });
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.VEINAMITOL.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.BLOOD_BAG.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.SYRINGE.getId(), ITEM_TEXTURE_RESOURCE);
        event.getExistingFileHelper().trackGenerated(BloodSystemMod.location(BloodSystemMod.SYRINGE.getId().getPath() + "_filled"), ITEM_TEXTURE_RESOURCE);
        event.getGenerator().addProvider(event.includeClient(), new ItemModelProvider(event.getGenerator(), BloodSystemMod.MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerModels() {
                this.basicItem(BloodSystemMod.VEINAMITOL.get());
                this.basicItem(BloodSystemMod.BLOOD_BAG.get());
                ItemModelBuilder emptySyringeModel = this.basicItem(BloodSystemMod.SYRINGE.get());
                ItemModelBuilder filledSyringeModel = this.alternateTextureItem(BloodSystemMod.SYRINGE.get(), "_filled");
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
    }
}
