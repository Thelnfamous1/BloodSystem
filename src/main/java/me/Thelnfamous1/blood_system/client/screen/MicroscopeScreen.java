package me.Thelnfamous1.blood_system.client.screen;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.block.entity.MicroscopeBlockEntity;
import me.Thelnfamous1.blood_system.common.menu.MicroscopeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MicroscopeScreen extends AbstractBloodAnalyzerScreen<MicroscopeMenu> {
   private static final ResourceLocation TEXTURE = BloodSystemMod.location("textures/gui/container/microscope.png");

   public MicroscopeScreen(MicroscopeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle, TEXTURE, Component.translatable(MicroscopeBlockEntity.START_BUTTON_KEY));
   }
}