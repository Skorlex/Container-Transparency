package skorlex.containertransparency.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PotionElementsScreen extends Screen {
    private final Screen parent;

    public PotionElementsScreen(Screen parent) {
        super(Component.translatable("options.containertransparency.title.potion"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int centerX = this.width / 2 - (buttonWidth / 2);
        int startY = this.height / 2 - 30;
        int spacing = 24;

        this.addRenderableWidget(Button.builder(Component.translatable("options.containertransparency.category.potion.container"), button -> this.minecraft.gui.setScreen(new ContainerPotionSettingsScreen(this))).bounds(centerX, startY, buttonWidth, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("options.containertransparency.category.potion.hud"), button -> this.minecraft.gui.setScreen(new HudPotionSettingsScreen(this))).bounds(centerX, startY + spacing, buttonWidth, 20).build());

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