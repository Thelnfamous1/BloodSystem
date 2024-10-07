package me.Thelnfamous1.blood_system.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.blood_system.BloodSystemMod;
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
   public static final int BATTERY_CHARGE_A_X_OFFSET = 49;
   public static final int BATTERY_CHARGE_A_Y_OFFSET = 21;
   public static final int BATTERY_CHARGE_B_X_OFFSET = 49;
   public static final int BATTERY_CHARGE_B_Y_OFFSET = 42;
   public static final int BATTERY_CHARGE_U_OFFSET = 196;
   public static final int BATTERY_CHARGE_V_OFFSET = 0;
   public static final int BATTERY_CHARGE_HEIGHT = 15;
   public static final int START_BUTTON_X_OFFSET = 147;
   public static final int START_BUTTON_Y_OFFSET = 55;
   public static final int START_BUTTON_WIDTH = 17;
   public static final int START_BUTTON_HEIGHT = 12;
   private final ResourceLocation texture;
   private StartAnalysisButton startButton;

   public AbstractBloodAnalyzerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
      super(pMenu, pPlayerInventory, pTitle);
      this.texture = pTexture;
   }

   @Override
   public void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
      this.startButton = new StartAnalysisButton(this.leftPos + START_BUTTON_X_OFFSET, this.topPos + START_BUTTON_Y_OFFSET, START_BUTTON_WIDTH, START_BUTTON_HEIGHT,
              Component.translatable(BloodSystemMod.translationKey("container", "blood_analyzer.start")),
              b -> {
                 if (this.menu.clickMenuButton(this.minecraft.player, AbstractBloodAnalyzerMenu.START_BUTTON_ID)) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, AbstractBloodAnalyzerMenu.START_BUTTON_ID);
                 }
              });
      this.startButton.active = this.canUseStartButton();
      this.addRenderableWidget(this.startButton);
   }

   private boolean canUseStartButton() {
      if(this.menu.isAnalyzing()){
         return true;
      }
      return this.menu.hasEnoughChargeToStartAnalysis() && this.menu.getRecipe(this.minecraft.level) != null;
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
      if (this.menu.isCharged()) {
         this.drawChargeProgress(pPoseStack, x, y, this.menu.getChargeProgressA(), BATTERY_CHARGE_A_X_OFFSET, BATTERY_CHARGE_A_Y_OFFSET);
         this.drawChargeProgress(pPoseStack, x, y, this.menu.getChargeProgressB(), BATTERY_CHARGE_B_X_OFFSET, BATTERY_CHARGE_B_Y_OFFSET);
      }

      int analysisProgress = this.menu.getAnalysisProgress();
      // Blood red, R=120, G=6, and B=6
      RenderSystem.setShaderColor(120.0F/256.0F, 6.0F/256.0F, 6.0F/256.0F, 1.0F);
      this.blit(pPoseStack, x + ANALYSIS_BAR_X_OFFSET, y + ANALYSIS_BAR_Y_OFFSET, ANALYSIS_BAR_U_OFFSET, ANALYSIS_BAR_V_OFFSET, ANALYSIS_BAR_WIDTH, analysisProgress);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void drawChargeProgress(PoseStack pPoseStack, int x, int y, int chargeProgress, int xOffset, int yOffset) {
       this.blit(pPoseStack, x + xOffset, y + yOffset, BATTERY_CHARGE_U_OFFSET, BATTERY_CHARGE_V_OFFSET, chargeProgress, BATTERY_CHARGE_HEIGHT);
   }

   @Override
   protected void containerTick() {
      super.containerTick();
      this.startButton.active = this.canUseStartButton();
   }
}