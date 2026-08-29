package skorlex.containertransparency.config;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudPotionSettingsScreen extends Screen {
    private final Screen parent;

    public HudPotionSettingsScreen(Screen parent) {
        super(Component.translatable("options.containertransparency.title.potion"));
        this.parent = parent;
    }

    private Component formatValue(Component caption, Double value) {
        int percent = (int) (value * 100);
        if (percent <= 0) return Component.empty().append(caption).append(": ").append(Component.literal("OFF"));
        else if (percent >= 100) return Component.empty().append(caption).append(": ").append(Component.literal("Default"));
        else return Component.empty().append(caption).append(": ").append(Component.literal(percent + "%"));
    }

    @Override
    protected void init() {
        OptionInstance<Double> iconOption = new OptionInstance<>("options.container-transparency.hud.potion.icon", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.hud.potion.icon.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.hudPotionIconOpacity, value -> { ContainerTransparencyConfig.hudPotionIconOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> bgOption = new OptionInstance<>("options.container-transparency.hud.potion.background", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.hud.potion.background.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.hudPotionBackgroundOpacity, value -> { ContainerTransparencyConfig.hudPotionBackgroundOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });

        int buttonWidth = 200;
        int centerX = this.width / 2 - (buttonWidth / 2);
        int startY = this.height / 2 - 30;
        int spacing = 24;

        this.addRenderableWidget(iconOption.createButton(this.minecraft.options, centerX, startY, buttonWidth));
        this.addRenderableWidget(bgOption.createButton(this.minecraft.options, centerX, startY + spacing, buttonWidth));

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> this.minecraft.gui.setScreen(this.parent)).bounds(centerX, this.height - 40, buttonWidth, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
    }

    @Override
    public void onClose() { this.minecraft.gui.setScreen(this.parent); }
}