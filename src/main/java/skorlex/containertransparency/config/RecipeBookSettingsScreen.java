package skorlex.containertransparency.config;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RecipeBookSettingsScreen extends Screen {
    private final Screen parent;

    public RecipeBookSettingsScreen(Screen parent) {
        super(Component.translatable("options.containertransparency.title.recipe_book"));
        this.parent = parent;
    }

    private Component formatValue(Component caption, Double value) {
        int percent = (int) (value * 100);
        if (value <= 0.0) return Component.empty().append(caption).append(": ").append(Component.literal("OFF"));
        else if (value >= 1.0) return Component.empty().append(caption).append(": ").append(Component.literal("Default"));
        else return Component.empty().append(caption).append(": ").append(Component.literal(percent + "%"));
    }

    @Override
    protected void init() {
        OptionInstance<Double> toggleOption = new OptionInstance<>("options.container-transparency.recipe_book.toggle", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.recipe_book.toggle.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.recipeBookToggleOpacity, value -> { ContainerTransparencyConfig.recipeBookToggleOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> slotOption = new OptionInstance<>("options.container-transparency.recipe_book.slot", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.recipe_book.slot.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.recipeSlotOpacity, value -> { ContainerTransparencyConfig.recipeSlotOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });

        int buttonWidth = 200;
        int centerX = this.width / 2 - (buttonWidth / 2);
        int startY = this.height / 2 - 28;
        int spacing = 24;

        this.addRenderableWidget(toggleOption.createButton(this.minecraft.options, centerX, startY, buttonWidth));
        this.addRenderableWidget(slotOption.createButton(this.minecraft.options, centerX, startY + spacing, buttonWidth));

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