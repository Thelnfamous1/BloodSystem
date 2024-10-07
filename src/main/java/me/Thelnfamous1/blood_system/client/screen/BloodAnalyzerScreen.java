package me.Thelnfamous1.blood_system.client.screen;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.block.entity.BloodAnalyzerBlockEntity;
import me.Thelnfamous1.blood_system.common.menu.BloodAnalyzerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BloodAnalyzerScreen extends AbstractBloodAnalyzerScreen<BloodAnalyzerMenu> {
   private static final ResourceLocation TEXTURE = BloodSystemMod.location("textures/gui/container/blood_analyzer.png");

   public BloodAnalyzerScreen(BloodAnalyzerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle, TEXTURE, Component.translatable(BloodAnalyzerBlockEntity.START_BUTTON_KEY));
   }
}