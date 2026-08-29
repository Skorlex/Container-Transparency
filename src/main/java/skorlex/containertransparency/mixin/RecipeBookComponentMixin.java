package skorlex.containertransparency.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skorlex.containertransparency.util.RenderState;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    // Trigger exactly when the search box is about to be drawn
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", shift = At.Shift.BEFORE))
    private void onSearchBoxRenderHead(CallbackInfo ci) {
        RenderState.isDrawingRecipeBookSearch = true;
    }

    // Turn off exactly when the search box finishes drawing
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", shift = At.Shift.AFTER))
    private void onSearchBoxRenderReturn(CallbackInfo ci) {
        RenderState.isDrawingRecipeBookSearch = false;
    }
}