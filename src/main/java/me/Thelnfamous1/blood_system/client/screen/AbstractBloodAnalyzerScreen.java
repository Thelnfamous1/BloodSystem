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
         this.blit(pPoseStack, x + 56, y + 36 + 12 - litProgress, 176, 12 - litProgress, 14, litProgress + 1);
      }

      int burnProgress = this.menu.getBurnProgress();
      this.blit(pPoseStack, x + 79, y + 34, 176, 14, burnProgress + 1, 16);
   }
}