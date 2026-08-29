package skorlex.containertransparency;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper; // Corrected import!
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import skorlex.containertransparency.config.ContainerTransparencyConfig;
import skorlex.containertransparency.config.ContainerTransparencyConfigScreen;

public class ContainerTransparencyClient implements ClientModInitializer {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("container-transparency", "key_category")
    );

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        ContainerTransparencyConfig.load();

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.containertransparency.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.gui.screen() == null) {
                    client.gui.setScreen(new ContainerTransparencyConfigScreen(null));
                }
            }
        });
    }
}