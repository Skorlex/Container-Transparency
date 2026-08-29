package skorlex.containertransparency.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skorlex.containertransparency.config.ContainerTransparencyConfig;
import skorlex.containertransparency.util.RenderState;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Unique private static boolean isDrawingItemCount = false;

    @Unique private static int containerDepth = 0;
    @Unique private static int slotHighlightDepth = 0;
    @Unique private static int hotbarSelectionDepth = 0;

    @Unique private static int hudPotionIconDepth = 0;
    @Unique private static int hudPotionBackgroundDepth = 0;
    @Unique private static int containerPotionIconDepth = 0;
    @Unique private static int containerPotionBackgroundDepth = 0;

    @Unique private static int recipeBookToggleDepth = 0;
    @Unique private static int recipeSlotDepth = 0;

    @Unique
    private int applyTransparency(int originalColor) {
        // Master Toggle Check
        if (!ContainerTransparencyConfig.isEnabled) {
            return originalColor;
        }

        float activeTransparency = -1.0F;

        if (RenderState.isDrawingRecipeBookSearch) {
            activeTransparency = ContainerTransparencyConfig.recipeBookSearchOpacity;
        } else if (isDrawingItemCount) {
            activeTransparency = RenderState.isDrawingHud ? ContainerTransparencyConfig.hudItemCountOpacity : ContainerTransparencyConfig.containerItemCountOpacity;
        } else if (RenderState.isDrawingContainerPotionText) {
            activeTransparency = ContainerTransparencyConfig.containerPotionTextOpacity;
        } else if (slotHighlightDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.slotHighlightOpacity;
        } else if (hotbarSelectionDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.hotbarSelectionOpacity;
        } else if (hudPotionIconDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.hudPotionIconOpacity;
        } else if (hudPotionBackgroundDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.hudPotionBackgroundOpacity;
        } else if (containerPotionIconDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.containerPotionIconOpacity;
        } else if (containerPotionBackgroundDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.containerPotionBackgroundOpacity;
        } else if (recipeSlotDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.recipeSlotOpacity;
        } else if (recipeBookToggleDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.recipeBookToggleOpacity;
        } else if (containerDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.transparency;
        } else if (RenderState.isDrawingHud) {
            activeTransparency = ContainerTransparencyConfig.hudOpacity;
        }

        if (activeTransparency >= 0.0F) {
            int currentAlpha = (originalColor >> 24) & 0xFF;
            if (currentAlpha == 0 && originalColor != 0) {
                currentAlpha = 255;
            }
            int newAlpha = (int) (currentAlpha * activeTransparency);
            return (originalColor & 0x00FFFFFF) | (newAlpha << 24);
        }
        return originalColor;
    }

    @Unique
    private void pushTarget(Identifier location) {
        if (location == null) return;
        String path = location.getPath();

        if (path.contains("slot_highlight")) {
            slotHighlightDepth++;
        } else if (path.contains("hotbar_selection")) {
            hotbarSelectionDepth++;
        } else if (RenderState.isDrawingHud && path.contains("effect_background")) {
            hudPotionBackgroundDepth++;
        } else if (RenderState.isDrawingHud && path.startsWith("mob_effect/")) {
            hudPotionIconDepth++;
        } else if (path.contains("effect_background")) {
            containerPotionBackgroundDepth++;
        } else if (path.startsWith("mob_effect/")) {
            containerPotionIconDepth++;
        } else if (path.startsWith("recipe_book/slot_")) {
            recipeSlotDepth++;
        } else if (path.startsWith("recipe_book/button")) {
            recipeBookToggleDepth++;
        } else if ((path.contains("container/") || path.contains("recipe_book/")) && !path.contains("hud/")) {
            // FIX: Added the trailing slash to "container/" so we don't accidentally fade the Mod Menu icon!
            boolean isGamemodeSwitcher = Minecraft.getInstance().gui.screen() != null && Minecraft.getInstance().gui.screen().getClass().getSimpleName().contains("GameModeSwitcher");
            if (!isGamemodeSwitcher) {
                containerDepth++;
            }
        }
    }

    @Unique
    private void popTarget(Identifier location) {
        if (location == null) return;
        String path = location.getPath();

        if (path.contains("slot_highlight")) {
            slotHighlightDepth--;
        } else if (path.contains("hotbar_selection")) {
            hotbarSelectionDepth--;
        } else if (RenderState.isDrawingHud && path.contains("effect_background")) {
            hudPotionBackgroundDepth--;
        } else if (RenderState.isDrawingHud && path.startsWith("mob_effect/")) {
            hudPotionIconDepth--;
        } else if (path.contains("effect_background")) {
            containerPotionBackgroundDepth--;
        } else if (path.startsWith("mob_effect/")) {
            containerPotionIconDepth--;
        } else if (path.startsWith("recipe_book/slot_")) {
            recipeSlotDepth--;
        } else if (path.startsWith("recipe_book/button")) {
            recipeBookToggleDepth--;
        } else if ((path.contains("container/") || path.contains("recipe_book/")) && !path.contains("hud/")) {
            boolean isGamemodeSwitcher = Minecraft.getInstance().gui.screen() != null && Minecraft.getInstance().gui.screen().getClass().getSimpleName().contains("GameModeSwitcher");
            if (!isGamemodeSwitcher) {
                containerDepth--;
            }
        }
    }

    @ModifyVariable(method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIFFFFI)V", at = @At("HEAD"), ordinal = 4, argsOnly = true)
    private int modifyBlitColorAlpha(int originalColor) { return applyTransparency(originalColor); }

    @ModifyVariable(method = "innerTiledBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIIIFFFFI)V", at = @At("HEAD"), ordinal = 6, argsOnly = true)
    private int modifyTiledBlitColorAlpha(int originalColor) { return applyTransparency(originalColor); }

    @ModifyVariable(method = "text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private int modifyStringTextColor(int originalColor) { return applyTransparency(originalColor); }

    @ModifyVariable(method = "text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private int modifyFormattedTextColor(int originalColor) { return applyTransparency(originalColor); }

    @Inject(method = "itemCount", at = @At("HEAD"))
    private void onItemCountHead(CallbackInfo ci) { isDrawingItemCount = true; }

    @Inject(method = "itemCount", at = @At("RETURN"))
    private void onItemCountReturn(CallbackInfo ci) { isDrawingItemCount = false; }

    @Inject(method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIFFFFI)V", at = @At("HEAD"))
    private void onInnerBlitIdentifierHead(RenderPipeline renderPipeline, Identifier location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color, CallbackInfo ci) { pushTarget(location); }

    @Inject(method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIFFFFI)V", at = @At("RETURN"))
    private void onInnerBlitIdentifierReturn(RenderPipeline renderPipeline, Identifier location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color, CallbackInfo ci) { popTarget(location); }

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V", at = @At("HEAD"))
    private void onBlitSpriteHead(RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, int color, CallbackInfo ci) { pushTarget(location); }

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V", at = @At("RETURN"))
    private void onBlitSpriteReturn(RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, int color, CallbackInfo ci) { popTarget(location); }

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIIII)V", at = @At("HEAD"))
    private void onBlitSpriteAdvancedHead(RenderPipeline renderPipeline, Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height, int color, CallbackInfo ci) { pushTarget(location); }

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIIII)V", at = @At("RETURN"))
    private void onBlitSpriteAdvancedReturn(RenderPipeline renderPipeline, Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height, int color, CallbackInfo ci) { popTarget(location); }
}