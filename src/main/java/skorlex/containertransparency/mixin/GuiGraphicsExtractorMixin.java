package skorlex.containertransparency.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skorlex.containertransparency.config.ContainerTransparencyConfig;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Unique
    private boolean containerTransparency$isRenderingTooltip = false;

    @Inject(method = "tooltip", at = @At("HEAD"))
    private void onBeforeTooltip(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, Identifier style, CallbackInfo ci) {
        this.containerTransparency$isRenderingTooltip = true;
    }

    @Inject(method = "tooltip", at = @At("RETURN"))
    private void onAfterTooltip(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, Identifier style, CallbackInfo ci) {
        this.containerTransparency$isRenderingTooltip = false;
    }

    @Unique
    private int applyTransparency(int originalColor) {
        if (this.containerTransparency$isRenderingTooltip) {
            return originalColor;
        }

        Minecraft client = Minecraft.getInstance();
        if (client != null && client.gui != null && client.gui.screen() instanceof AbstractContainerScreen) {
            int currentAlpha = (originalColor >> 24) & 0xFF;
            if (currentAlpha == 0) {
                currentAlpha = 255;
            }
            int newAlpha = (int) (currentAlpha * ContainerTransparencyConfig.transparency);
            return (originalColor & 0x00FFFFFF) | (newAlpha << 24);
        }
        return originalColor;
    }

    @ModifyVariable(
            method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIFFFFI)V",
            at = @At("HEAD"),
            ordinal = 4,
            argsOnly = true
    )
    private int modifyBlitColorAlpha(int originalColor) {
        return applyTransparency(originalColor);
    }

    @ModifyVariable(
            method = "innerTiledBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIIIFFFFI)V",
            at = @At("HEAD"),
            ordinal = 6,
            argsOnly = true
    )
    private int modifyTiledBlitColorAlpha(int originalColor) {
        return applyTransparency(originalColor);
    }
}