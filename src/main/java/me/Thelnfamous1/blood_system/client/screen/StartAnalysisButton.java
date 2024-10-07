package me.Thelnfamous1.blood_system.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.menu.AbstractBloodAnalyzerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StartAnalysisButton<T extends AbstractBloodAnalyzerMenu> extends Button {
    private static final ResourceLocation TEXTURE = BloodSystemMod.location("textures/gui/container/blood_analyzer.png");
    public static final int TEXTURE_U = 177;
    public static final int TEXTURE_V = 0;
    public static final int TEXTURE_V_OFFSET = 12;
    public static final int INACTIVE_Y_IMAGE = 1;
    public static final int ACTIVATED_Y_IMAGE = 3;
    public static final int HOVERED_Y_IMAGE = 2;
    public static final int OFF_Y_IMAGE = 0;
    private final AbstractBloodAnalyzerScreen<T> screen;

    public StartAnalysisButton(AbstractBloodAnalyzerScreen<T> screen, int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.screen = screen;
    }

    public StartAnalysisButton(AbstractBloodAnalyzerScreen<T> screen, int pX, int pY, int pWidth, int pHeight, Component pMessage, Button.OnPress pOnPress, Button.OnTooltip pOnTooltip) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
        this.screen = screen;
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

    // 0 - the off texture
    // 1 - the inactive texture
    // 2 - the hovered texture
    // 3 - the on texture
    @Override
    protected int getYImage(boolean pIsHovered) {
        if (!this.active) {
            return INACTIVE_Y_IMAGE;
        } else if (this.screen.getMenu().isActivated()) {
            return ACTIVATED_Y_IMAGE;
        } else if(pIsHovered){
            return HOVERED_Y_IMAGE;
        }

        return OFF_Y_IMAGE;
    }
}
