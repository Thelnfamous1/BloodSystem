package me.Thelnfamous1.blood_system.common.datagen;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.capability.BloodType;
import me.Thelnfamous1.blood_system.common.command.BloodSystemCommands;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BloodSystemDatagen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new LanguageProvider(event.getGenerator(), BloodSystemMod.MODID, "en_us") {
            @Override
            protected void addTranslations() {
                this.add(BloodSystemMod.MAX_BLOOD.get().getDescriptionId(), "Max Blood");
                for(BloodType bloodType : BloodType.values()){
                    this.add(bloodType.getTranslationKey(), bloodType.getSerializedName());
                }
                this.add(BloodSystemCommands.GET_BLOOD_TYPE_SUCCESS, "%s has blood type %s");
                this.add(BloodSystemCommands.SET_BLOOD_TYPE_SUCCESS, "Set the blood type of %s to %s");
            }
        });
    }
}
