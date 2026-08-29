package skorlex.containertransparency.config;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ContainerPotionSettingsScreen extends Screen {
    private final Screen parent;

    public ContainerPotionSettingsScreen(Screen parent) {
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
        OptionInstance<Double> iconOption = new OptionInstance<>("options.container-transparency.container.potion.icon", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.container.potion.icon.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.containerPotionIconOpacity, value -> { ContainerTransparencyConfig.containerPotionIconOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> bgOption = new OptionInstance<>("options.container-transparency.container.potion.background", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.container.potion.background.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.containerPotionBackgroundOpacity, value -> { ContainerTransparencyConfig.containerPotionBackgroundOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> textOption = new OptionInstance<>("options.container-transparency.container.potion.text", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.container.potion.text.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.containerPotionTextOpacity, value -> { ContainerTransparencyConfig.containerPotionTextOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });

        int buttonWidth = 200;
        int centerX = this.width / 2 - (buttonWidth / 2);
        int startY = this.height / 2 - 40;
        int spacing = 24;

        this.addRenderableWidget(iconOption.createButton(this.minecraft.options, centerX, startY, buttonWidth));
        this.addRenderableWidget(bgOption.createButton(this.minecraft.options, centerX, startY + spacing, buttonWidth));
        this.addRenderableWidget(textOption.createButton(this.minecraft.options, centerX, startY + (spacing * 2), buttonWidth));

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