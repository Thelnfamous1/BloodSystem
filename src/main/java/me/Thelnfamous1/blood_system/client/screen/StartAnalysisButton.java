package me.Thelnfamous1.blood_system.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StartAnalysisButton extends Button {
    private static final ResourceLocation TEXTURE = BloodSystemMod.location("textures/gui/container/blood_analyzer.png");
    public static final int TEXTURE_U = 177;
    public static final int TEXTURE_V = 12;
    public static final int TEXTURE_V_OFFSET = 12;

    public StartAnalysisButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
    }

    public StartAnalysisButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, Button.OnPress pOnPress, Button.OnTooltip pOnTooltip) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int yImage = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(pPoseStack, this.x, this.y, TEXTURE_U, TEXTURE_V + yImage * TEXTURE_V_OFFSET, this.width, this.height);
        this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
        /*
        int fgColor = getFGColor();
        drawCenteredString(pPoseStack, minecraft.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, fgColor | Mth.ceil(this.alpha * 255.0F) << 24);
         */
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }
}
