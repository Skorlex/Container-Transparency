package skorlex.containertransparency.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ContainerSettingsScreen extends Screen {
    private final Screen parent;

    public ContainerSettingsScreen(Screen parent) {
        super(Component.translatable("options.containertransparency.title.container"));
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
        // Build the tooltip with the base description and the yellow example
        Component containerTooltipText = Component.empty()
                .append(Component.translatable("options.container-transparency.container.tooltip"))
                .append(Component.literal("\n\n"))
                .append(Component.literal("If you want a uniform look, set sliders to a similar percentage (e.g. Container Base & Slot Highlight both set to 50%.)\nNote: matching all sliders isn't always recommended.").withStyle(ChatFormatting.YELLOW));

        OptionInstance<Double> transparencyOption = new OptionInstance<>("options.container-transparency.container", OptionInstance.cachedConstantTooltip(containerTooltipText), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.transparency, value -> { ContainerTransparencyConfig.transparency = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> slotOption = new OptionInstance<>("options.container-transparency.slot", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.slot.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.slotHighlightOpacity, value -> { ContainerTransparencyConfig.slotHighlightOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> itemCountOption = new OptionInstance<>("options.container-transparency.container.count", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.container.count.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.containerItemCountOpacity, value -> { ContainerTransparencyConfig.containerItemCountOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });

        int fullWidth = 310;
        int halfWidth = 150;
        int center = this.width / 2;
        int leftX = center - 155;
        int rightX = center + 5;
        int startY = this.height / 2 - 52;
        int spacing = 24;

        // Big Main Slider spanning both columns
        this.addRenderableWidget(transparencyOption.createButton(this.minecraft.options, leftX, startY, fullWidth));

        // Row 2: Secondary Options
        this.addRenderableWidget(slotOption.createButton(this.minecraft.options, leftX, startY + spacing, halfWidth));
        this.addRenderableWidget(itemCountOption.createButton(this.minecraft.options, rightX, startY + spacing, halfWidth));

        // Row 3: Sub-menus
        this.addRenderableWidget(Button.builder(Component.translatable("options.containertransparency.category.recipe_book"), button -> this.minecraft.gui.setScreen(new RecipeBookSettingsScreen(this))).bounds(leftX, startY + (spacing * 2), halfWidth, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("options.containertransparency.category.potion"), button -> this.minecraft.gui.setScreen(new ContainerPotionSettingsScreen(this))).bounds(rightX, startY + (spacing * 2), halfWidth, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> this.minecraft.gui.setScreen(this.parent)).bounds(center - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
    }

    @Override
    public void onClose() { this.minecraft.gui.setScreen(this.parent); }
}