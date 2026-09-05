package skorlex.containertransparency.config;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ContainerTransparencyConfigScreen extends Screen {
    private final Screen parent;
    private final List<AbstractWidget> subMenuButtons = new ArrayList<>();

    public ContainerTransparencyConfigScreen(Screen parent) {
        super(Component.translatable("options.containertransparency.title"));
        this.parent = parent;
    }

    private Component formatValue(Component caption, Double value) {
        int percent = (int) (value * 100);
        if (value <= 0.0) return Component.empty().append(caption).append(": ").append(Component.literal("OFF"));
        else if (value >= 1.0) return Component.empty().append(caption).append(": ").append(Component.literal("Default"));
        else return Component.empty().append(caption).append(": ").append(Component.literal(percent + "%"));
    }

    // FIX: Takes a strict Integer (0 - 200) and displays it perfectly without decimal jumps
    private Component formatTextValue(Component caption, Integer value) {
        if (value == 100) return Component.empty().append(caption).append(": ").append(Component.literal("Default"));
        else return Component.empty().append(caption).append(": ").append(Component.literal(value + "%"));
    }

    @Override
    protected void init() {
        this.subMenuButtons.clear();

        OptionInstance<Double> transparencyOption = new OptionInstance<>("options.container-transparency.container", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.container.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.transparency, value -> { ContainerTransparencyConfig.transparency = value.floatValue(); ContainerTransparencyConfig.save(); });
        OptionInstance<Double> slotOption = new OptionInstance<>("options.container-transparency.slot", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.slot.tooltip")), this::formatValue, OptionInstance.UnitDouble.INSTANCE, (double) ContainerTransparencyConfig.slotHighlightOpacity, value -> { ContainerTransparencyConfig.slotHighlightOpacity = value.floatValue(); ContainerTransparencyConfig.save(); });

        // FIX: The text slider now uses IntRange(0, 200) for exact 1% physical pixel snapping
        OptionInstance<Integer> textOption = new OptionInstance<>("options.container-transparency.container.text", OptionInstance.cachedConstantTooltip(Component.translatable("options.container-transparency.container.text.tooltip")), this::formatTextValue, new OptionInstance.IntRange(0, 200), Math.round(ContainerTransparencyConfig.containerTextBrightness * 200), value -> { ContainerTransparencyConfig.containerTextBrightness = value / 200.0F; ContainerTransparencyConfig.save(); });

        int fullWidth = 310;
        int halfWidth = 150;
        int center = this.width / 2;
        int leftX = center - 155;
        int rightX = center + 5;
        int startY = this.height / 2 - 52;
        int spacing = 24;

        AbstractWidget containerSlider = transparencyOption.createButton(this.minecraft.options, leftX, startY, fullWidth);
        this.subMenuButtons.add(containerSlider);
        this.addRenderableWidget(containerSlider);

        AbstractWidget slotSlider = slotOption.createButton(this.minecraft.options, leftX, startY + spacing, halfWidth);
        this.subMenuButtons.add(slotSlider);
        this.addRenderableWidget(slotSlider);

        AbstractWidget textSlider = textOption.createButton(this.minecraft.options, rightX, startY + spacing, halfWidth);
        this.subMenuButtons.add(textSlider);
        this.addRenderableWidget(textSlider);

        Button recipeBtn = Button.builder(Component.translatable("options.containertransparency.category.recipe_book"), button -> this.minecraft.gui.setScreen(new RecipeBookSettingsScreen(this))).bounds(leftX, startY + (spacing * 2), halfWidth, 20).build();
        this.subMenuButtons.add(recipeBtn);
        this.addRenderableWidget(recipeBtn);

        Button potionBtn = Button.builder(Component.translatable("options.containertransparency.category.potion"), button -> this.minecraft.gui.setScreen(new PotionElementsScreen(this))).bounds(rightX, startY + (spacing * 2), halfWidth, 20).build();
        this.subMenuButtons.add(potionBtn);
        this.addRenderableWidget(potionBtn);

        CycleButton<Boolean> masterToggle = CycleButton.onOffBuilder(ContainerTransparencyConfig.isEnabled)
                .create(10, this.height - 30, 110, 20, Component.translatable("options.container-transparency.master_toggle"), (button, value) -> {
                    ContainerTransparencyConfig.isEnabled = value;
                    ContainerTransparencyConfig.save();
                    this.updateButtonStates();
                });
        this.addRenderableWidget(masterToggle);

        Button resetBtn = Button.builder(Component.literal("Reset Defaults"), button -> {
            ContainerTransparencyConfig.reset();
            this.minecraft.gui.setScreen(new ContainerTransparencyConfigScreen(this.parent));
        }).bounds(125, this.height - 30, 110, 20).build();
        this.addRenderableWidget(resetBtn);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.minecraft.gui.setScreen(this.parent)).bounds(this.width - 120, this.height - 30, 110, 20).build());

        this.updateButtonStates();
    }

    private void updateButtonStates() {
        for (AbstractWidget widget : this.subMenuButtons) {
            widget.active = ContainerTransparencyConfig.isEnabled;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}