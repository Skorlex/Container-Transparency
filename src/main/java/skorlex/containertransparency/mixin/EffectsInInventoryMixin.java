package skorlex.containertransparency.mixin;

import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skorlex.containertransparency.util.RenderState;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {

    // Turns ON exactly when the potion text starts drawing
    @Inject(method = "extractText", at = @At("HEAD"))
    private void onExtractTextHead(CallbackInfo ci) {
        RenderState.isDrawingContainerPotionText = true;
    }

    // Turns OFF exactly when the potion text finishes drawing
    @Inject(method = "extractText", at = @At("RETURN"))
    private void onExtractTextReturn(CallbackInfo ci) {
        RenderState.isDrawingContainerPotionText = false;
    }
}