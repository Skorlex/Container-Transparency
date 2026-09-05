package skorlex.containertransparency.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skorlex.containertransparency.util.RenderState;

// We target the parent class AND the specific screens that override the method without calling super!
@Mixin({AbstractContainerScreen.class, InventoryScreen.class, CreativeModeInventoryScreen.class, MerchantScreen.class})
public class AbstractContainerScreenMixin {

    @Inject(method = "extractLabels", at = @At("HEAD"))
    private void onExtractLabelsHead(GuiGraphicsExtractor graphics, int xm, int ym, CallbackInfo ci) {
        RenderState.isDrawingContainerText = true;
    }

    @Inject(method = "extractLabels", at = @At("RETURN"))
    private void onExtractLabelsReturn(GuiGraphicsExtractor graphics, int xm, int ym, CallbackInfo ci) {
        RenderState.isDrawingContainerText = false;
    }
}