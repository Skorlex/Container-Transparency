package skorlex.containertransparency.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skorlex.containertransparency.config.ContainerTransparencyConfig;

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityOptionsScreenMixin {

    @Unique
    private static final OptionInstance<Double> CONTAINER_TRANSPARENCY_OPTION = new OptionInstance<>(
            "options.container-transparency",
            OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.tooltip")),
            (caption, value) -> Component.empty()
                    .append(caption)
                    .append(": ")
                    .append(Component.literal((int) (value * 100) + "%")),
            OptionInstance.UnitDouble.INSTANCE,
            (double) ContainerTransparencyConfig.transparency,
            value -> {
                ContainerTransparencyConfig.transparency = value.floatValue();
                ContainerTransparencyConfig.save();
            }
    );

    @Inject(method = "options", at = @At("RETURN"), cancellable = true)
    private static void addContainerTransparencyOption(Options options, CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        OptionInstance<?>[] original = cir.getReturnValue();
        OptionInstance<?>[] newOptions = new OptionInstance<?>[original.length + 1];
        System.arraycopy(original, 0, newOptions, 0, original.length);
        newOptions[original.length] = CONTAINER_TRANSPARENCY_OPTION;
        cir.setReturnValue(newOptions);
    }
}