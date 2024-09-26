package me.Thelnfamous1.blood_system.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.capability.BloodCapability;
import me.Thelnfamous1.blood_system.common.capability.BloodCapabilityProvider;
import me.Thelnfamous1.blood_system.common.config.BloodSystemConfig;
import me.Thelnfamous1.blood_system.common.config.RenderCorner;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BloodMeterOverlay implements IGuiOverlay {

    public static BloodMeterOverlay INSTANCE = new BloodMeterOverlay();
    public static final ResourceLocation BLOOD_METER_EMPTY_ICON_TEXTURE = BloodSystemMod.location("textures/gui/blood_drop_empty.png");
    public static final ResourceLocation BLOOD_METER_FULL_ICON_TEXTURE = BloodSystemMod.location("textures/gui/blood_drop_full.png");
    private static final int BLOOD_METER_ICON_WIDTH = 33;
    private static final int BLOOD_METER_ICON_HEIGHT = 33;

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        RenderCorner renderCorner = BloodSystemConfig.CLIENT.bloodMeterRenderCorner.get();
        int xPos = renderCorner.getXPos(BloodSystemConfig.CLIENT.bloodMeterXOffset.get(), screenWidth);
        int yPos = renderCorner.getYPos(BloodSystemConfig.CLIENT.bloodMeterYOffset.get(), screenHeight);

        if (!gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())
        {
            gui.setupOverlayRenderState(true, false);
            RenderSystem.enableBlend();

            Entity cameraEntity = gui.getMinecraft().getCameraEntity();
            if(cameraEntity instanceof Player player){
                // draw empty icon texture
                RenderSystem.setShaderTexture(0, BLOOD_METER_EMPTY_ICON_TEXTURE);
                GuiComponent.blit(poseStack, xPos, yPos, 0, 0, BLOOD_METER_ICON_WIDTH, BLOOD_METER_ICON_HEIGHT, BLOOD_METER_ICON_WIDTH, BLOOD_METER_ICON_HEIGHT);

                // blit full icon texture
                float bloodRatio = BloodCapabilityProvider.getCapability(player).map(BloodCapability::getBloodRatio).orElse(0.0F);
                int bloodLevel = Mth.floor(BLOOD_METER_ICON_HEIGHT * bloodRatio);
                int bloodLevelRemaining = BLOOD_METER_ICON_HEIGHT - bloodLevel;
                RenderSystem.setShaderTexture(0, BLOOD_METER_FULL_ICON_TEXTURE);
                GuiComponent.blit(poseStack, xPos, yPos + bloodLevelRemaining, 0, bloodLevelRemaining, BLOOD_METER_ICON_WIDTH, bloodLevel, BLOOD_METER_ICON_WIDTH, BLOOD_METER_ICON_HEIGHT);
            }

            RenderSystem.disableBlend();
        }
    }
}
