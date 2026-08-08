package skorlex.containertransparency.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skorlex.containertransparency.config.ContainerTransparencyConfig;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Unique
    private static int transparencyDepth = 0;

    @Unique
    private int applyTransparency(int originalColor) {
        // Only apply transparency if our depth counter is active
        if (transparencyDepth > 0) {
            int currentAlpha = (originalColor >> 24) & 0xFF;
            if (currentAlpha == 0) {
                currentAlpha = 255;
            }
            int newAlpha = (int) (currentAlpha * ContainerTransparencyConfig.transparency);
            return (originalColor & 0x00FFFFFF) | (newAlpha << 24);
        }
        return originalColor;
    }

    @Unique
    private boolean isTarget(Identifier location) {
        if (location == null) return false;
        String path = location.getPath();
        return path.contains("container") ||
                path.contains("recipe_book") ||
                path.contains("effect_background") ||
                path.contains("text_field");
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

    // 1. Catch legacy containers
    @Inject(
            method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIFFFFI)V",
            at = @At("HEAD")
    )
    private void onInnerBlitIdentifierHead(RenderPipeline renderPipeline, Identifier location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color, CallbackInfo ci) {
        if (isTarget(location)) transparencyDepth++;
    }

    @Inject(
            method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIFFFFI)V",
            at = @At("RETURN")
    )
    private void onInnerBlitIdentifierReturn(RenderPipeline renderPipeline, Identifier location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color, CallbackInfo ci) {
        if (isTarget(location)) transparencyDepth--;
    }

    // 2. Catch standard blitSprite calls
    @Inject(
            method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V",
            at = @At("HEAD")
    )
    private void onBlitSpriteHead(RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, int color, CallbackInfo ci) {
        if (isTarget(location)) transparencyDepth++;
    }

    @Inject(
            method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V",
            at = @At("RETURN")
    )
    private void onBlitSpriteReturn(RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, int color, CallbackInfo ci) {
        if (isTarget(location)) transparencyDepth--;
    }

    // 3. Catch complex/sliced blitSprite calls
    @Inject(
            method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIIII)V",
            at = @At("HEAD")
    )
    private void onBlitSpriteAdvancedHead(RenderPipeline renderPipeline, Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height, int color, CallbackInfo ci) {
        if (isTarget(location)) transparencyDepth++;
    }

    @Inject(
            method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIIII)V",
            at = @At("RETURN")
    )
    private void onBlitSpriteAdvancedReturn(RenderPipeline renderPipeline, Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height, int color, CallbackInfo ci) {
        if (isTarget(location)) transparencyDepth--;
    }
}