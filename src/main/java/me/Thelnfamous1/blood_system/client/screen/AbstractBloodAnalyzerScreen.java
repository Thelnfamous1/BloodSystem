package me.Thelnfamous1.blood_system.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.blood_system.common.menu.AbstractBloodAnalyzerMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractBloodAnalyzerScreen<T extends AbstractBloodAnalyzerMenu> extends AbstractContainerScreen<T> {
   public static final int ANALYSIS_BAR_X_OFFSET = 98;
   public static final int ANALYSIS_BAR_Y_OFFSET = 17;
   public static final int ANALYSIS_BAR_U_OFFSET = 221;
   public static final int ANALYSIS_BAR_V_OFFSET = 0;
   public static final int ANALYSIS_BAR_WIDTH = 7;
   public static final int BATTERY_POWER_X_OFFSET = 50;
   public static final int BATTERY_POWER_Y_OFFSET = 21;
   public static final int BATTERY_POWER_U_OFFSET = 197;
   public static final int BATTERY_POWER_V_OFFSET = 0;
   public static final int BATTERY_POWER_HEIGHT = 15;
   private final ResourceLocation texture;

   public AbstractBloodAnalyzerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
      super(pMenu, pPlayerInventory, pTitle);
      this.texture = pTexture;
   }

   @Override
   public void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   @Override
   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(pPoseStack);
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(pPoseStack, pMouseX, pMouseY);
   }

   @Override
   protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.texture);
      int x = this.leftPos;
      int y = this.topPos;
      this.blit(pPoseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
      if (this.menu.isLit()) {
         int litProgress = this.menu.getLitProgress();
         this.blit(pPoseStack, x + BATTERY_POWER_X_OFFSET, y + BATTERY_POWER_Y_OFFSET, BATTERY_POWER_U_OFFSET, BATTERY_POWER_V_OFFSET, litProgress + 1, BATTERY_POWER_HEIGHT);
      }

      int burnProgress = this.menu.getBurnProgress();
      // Blood red, R=120, G=6, and B=6
      RenderSystem.setShaderColor(120.0F/256.0F, 6.0F/256.0F, 6.0F/256.0F, 1.0F);
      this.blit(pPoseStack, x + ANALYSIS_BAR_X_OFFSET, y + ANALYSIS_BAR_Y_OFFSET, ANALYSIS_BAR_U_OFFSET, ANALYSIS_BAR_V_OFFSET, ANALYSIS_BAR_WIDTH, burnProgress);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}