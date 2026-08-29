package skorlex.containertransparency.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
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

    @Override
    protected void init() {
        this.subMenuButtons.clear();
        int buttonWidth = 200;
        int centerX = this.width / 2 - (buttonWidth / 2);
        int startY = this.height / 2 - 40;
        int spacing = 24;

        CycleButton<Boolean> masterToggle = CycleButton.onOffBuilder(ContainerTransparencyConfig.isEnabled)
                .create(centerX, startY - spacing, buttonWidth, 20, Component.translatable("options.container-transparency.master_toggle"), (button, value) -> {
                    ContainerTransparencyConfig.isEnabled = value;
                    ContainerTransparencyConfig.save();
                    this.updateButtonStates();
                });
        this.addRenderableWidget(masterToggle);

        // Container Button with Tooltip
        Button containerBtn = Button.builder(Component.translatable("options.containertransparency.category.container"), button -> this.minecraft.gui.setScreen(new ContainerSettingsScreen(this)))
                .bounds(centerX, startY, buttonWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("options.containertransparency.category.container.tooltip")))
                .build();

        // HUD Button with Tooltip
        Button hudBtn = Button.builder(Component.translatable("options.containertransparency.category.hud"), button -> this.minecraft.gui.setScreen(new HudSettingsScreen(this)))
                .bounds(centerX, startY + spacing, buttonWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("options.containertransparency.category.hud.tooltip")))
                .build();

        this.subMenuButtons.add(containerBtn);
        this.subMenuButtons.add(hudBtn);

        this.addRenderableWidget(containerBtn);
        this.addRenderableWidget(hudBtn);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.minecraft.gui.setScreen(this.parent)).bounds(centerX, this.height - 40, buttonWidth, 20).build());

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