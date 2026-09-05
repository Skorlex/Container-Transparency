package skorlex.containertransparency.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

    @Unique private static int containerDepth = 0;
    @Unique private static int slotHighlightDepth = 0;

    @Unique private static int hudPotionIconDepth = 0;
    @Unique private static int hudPotionBackgroundDepth = 0;
    @Unique private static int containerPotionIconDepth = 0;
    @Unique private static int containerPotionBackgroundDepth = 0;

    @Unique private static int recipeBookToggleDepth = 0;
    @Unique private static int recipeSlotDepth = 0;
    @Unique private static int recipeBookFilterDepth = 0;

    @Unique
    private int applyTransparency(int originalColor) {
        if (!ContainerTransparencyConfig.isEnabled) {
            return originalColor;
        }

        // --- Container Text Perfectly Progressive HSB Logic ---
        if (RenderState.isDrawingContainerText) {
            float rawBrightness = ContainerTransparencyConfig.containerTextBrightness;

            if (Math.abs(rawBrightness - 0.5F) < 0.01F) {
                return originalColor;
            }

            int r = (originalColor >> 16) & 0xFF;
            int g = (originalColor >> 8) & 0xFF;
            int b = originalColor & 0xFF;

            float rf = r / 255.0F;
            float gf = g / 255.0F;
            float bf = b / 255.0F;
            float max = Math.max(rf, Math.max(gf, bf));
            float min = Math.min(rf, Math.min(gf, bf));
            float delta = max - min;

            float hue = 0.0F;
            float saturation = (max == 0.0F) ? 0.0F : (delta / max);
            float value = max;

            if (delta != 0.0F) {
                if (max == rf) hue = ((gf - bf) / delta) % 6.0F;
                else if (max == gf) hue = ((bf - rf) / delta) + 2.0F;
                else hue = ((rf - gf) / delta) + 4.0F;
                hue /= 6.0F;
                if (hue < 0.0F) hue += 1.0F;
            }

            // Applies a perfectly even, progressive linear fade across the entire slider range
            if (rawBrightness < 0.5F) {
                float factor = (0.5F - rawBrightness) * 2.0F;
                saturation = saturation * (1.0F - factor);
                value = value + (1.0F - value) * factor;
            } else {
                float factor = (rawBrightness - 0.5F) * 2.0F;
                value = value * (1.0F - factor);
            }

            int i = (int) (hue * 6.0F);
            float f = (hue * 6.0F) - i;
            float p = value * (1.0F - saturation);
            float q = value * (1.0F - f * saturation);
            float tValue = value * (1.0F - (1.0F - f) * saturation);

            float rOut = 0, gOut = 0, bOut = 0;
            switch (i % 6) {
                case 0: rOut = value; gOut = tValue; bOut = p; break;
                case 1: rOut = q; gOut = value; bOut = p; break;
                case 2: rOut = p; gOut = value; bOut = tValue; break;
                case 3: rOut = p; gOut = q; bOut = value; break;
                case 4: rOut = tValue; gOut = p; bOut = value; break;
                case 5: rOut = value; gOut = p; bOut = q; break;
            }

            int finalR = Math.max(0, Math.min(255, (int) (rOut * 255.0F)));
            int finalG = Math.max(0, Math.min(255, (int) (gOut * 255.0F)));
            int finalB = Math.max(0, Math.min(255, (int) (bOut * 255.0F)));

            return (originalColor & 0xFF000000) | (finalR << 16) | (finalG << 8) | finalB;
        }
        // -------------------------------------------------

        float activeTransparency = -1.0F;

        if (RenderState.isDrawingRecipeBookSearch || recipeBookFilterDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.transparency;
        } else if (RenderState.isDrawingContainerPotionText) {
            activeTransparency = ContainerTransparencyConfig.containerPotionTextOpacity;
        } else if (slotHighlightDepth > 0) {
            activeTransparency = ContainerTransparencyConfig.slotHighlightOpacity;
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
        String lowerPath = path.toLowerCase();

        if (lowerPath.contains("slot_highlight")) {
            slotHighlightDepth++;
        } else if (RenderState.isDrawingHud && lowerPath.contains("effect_background")) {
            hudPotionBackgroundDepth++;
        } else if (RenderState.isDrawingHud && lowerPath.startsWith("mob_effect/")) {
            hudPotionIconDepth++;
        } else if (lowerPath.contains("effect_background")) {
            containerPotionBackgroundDepth++;
        } else if (lowerPath.startsWith("mob_effect/")) {
            containerPotionIconDepth++;
        } else if (lowerPath.startsWith("recipe_book/slot_")) {
            recipeSlotDepth++;
        } else if (lowerPath.startsWith("recipe_book/button")) {
            recipeBookToggleDepth++;
        } else if (lowerPath.startsWith("recipe_book/filter")) {
            recipeBookFilterDepth++;
        } else if (!lowerPath.contains("hud/")) {

            Screen currentScreen = Minecraft.getInstance().gui.screen();
            boolean isGamemodeSwitcher = currentScreen != null && currentScreen.getClass().getSimpleName().contains("GameModeSwitcher");
            boolean isContainerScreen = currentScreen instanceof AbstractContainerScreen;

            if (!isGamemodeSwitcher && isContainerScreen) {
                boolean isContainer = lowerPath.contains("container")
                        || lowerPath.contains("gui")
                        || lowerPath.contains("recipe_book")
                        || lowerPath.contains("shulker")
                        || lowerPath.contains("barrel")
                        || lowerPath.contains("chest")
                        || lowerPath.contains("inventory")
                        || lowerPath.contains("furnace")
                        || lowerPath.contains("dispenser")
                        || lowerPath.contains("dropper")
                        || lowerPath.contains("hopper")
                        || lowerPath.contains("villager")
                        || lowerPath.contains("merchant")
                        || lowerPath.contains("crafter")
                        || lowerPath.contains("anvil")
                        || lowerPath.contains("loom")
                        || lowerPath.contains("cartography")
                        || lowerPath.contains("grindstone")
                        || lowerPath.contains("stonecutter")
                        || lowerPath.contains("smithing")
                        || lowerPath.contains("enchanting")
                        || lowerPath.contains("brewing")
                        || lowerPath.contains("beacon")
                        || lowerPath.contains("smoker")
                        || lowerPath.contains("bundle");
                boolean isWidget = lowerPath.startsWith("widget/");

                if (isContainer || isWidget) {
                    containerDepth++;
                }
            }
        }
    }

    @Unique
    private void popTarget(Identifier location) {
        if (location == null) return;
        String path = location.getPath();
        String lowerPath = path.toLowerCase();

        if (lowerPath.contains("slot_highlight")) {
            slotHighlightDepth--;
        } else if (RenderState.isDrawingHud && lowerPath.contains("effect_background")) {
            hudPotionBackgroundDepth--;
        } else if (RenderState.isDrawingHud && lowerPath.startsWith("mob_effect/")) {
            hudPotionIconDepth--;
        } else if (lowerPath.contains("effect_background")) {
            containerPotionBackgroundDepth--;
        } else if (lowerPath.startsWith("mob_effect/")) {
            containerPotionIconDepth--;
        } else if (lowerPath.startsWith("recipe_book/slot_")) {
            recipeSlotDepth--;
        } else if (lowerPath.startsWith("recipe_book/button")) {
            recipeBookToggleDepth--;
        } else if (lowerPath.startsWith("recipe_book/filter")) {
            recipeBookFilterDepth--;
        } else if (!lowerPath.contains("hud/")) {

            Screen currentScreen = Minecraft.getInstance().gui.screen();
            boolean isGamemodeSwitcher = currentScreen != null && currentScreen.getClass().getSimpleName().contains("GameModeSwitcher");
            boolean isContainerScreen = currentScreen instanceof AbstractContainerScreen;

            if (!isGamemodeSwitcher && isContainerScreen) {
                boolean isContainer = lowerPath.contains("container")
                        || lowerPath.contains("gui")
                        || lowerPath.contains("recipe_book")
                        || lowerPath.contains("shulker")
                        || lowerPath.contains("barrel")
                        || lowerPath.contains("chest")
                        || lowerPath.contains("inventory")
                        || lowerPath.contains("furnace")
                        || lowerPath.contains("dispenser")
                        || lowerPath.contains("dropper")
                        || lowerPath.contains("hopper")
                        || lowerPath.contains("villager")
                        || lowerPath.contains("merchant")
                        || lowerPath.contains("crafter")
                        || lowerPath.contains("anvil")
                        || lowerPath.contains("loom")
                        || lowerPath.contains("cartography")
                        || lowerPath.contains("grindstone")
                        || lowerPath.contains("stonecutter")
                        || lowerPath.contains("smithing")
                        || lowerPath.contains("enchanting")
                        || lowerPath.contains("brewing")
                        || lowerPath.contains("beacon")
                        || lowerPath.contains("smoker")
                        || lowerPath.contains("bundle");
                boolean isWidget = lowerPath.startsWith("widget/");

                if (isContainer || isWidget) {
                    containerDepth--;
                }
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