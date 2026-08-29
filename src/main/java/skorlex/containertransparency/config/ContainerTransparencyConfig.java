package skorlex.containertransparency.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ContainerTransparencyConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("container-transparency.json").toFile();

    // Master Toggle
    public static boolean isEnabled = true;

    // Container Variables
    public static float transparency = 1.0F;
    public static float slotHighlightOpacity = 1.0F;
    public static float containerItemCountOpacity = 1.0F;

    // Container Potion Variables
    public static float containerPotionIconOpacity = 1.0F;
    public static float containerPotionBackgroundOpacity = 1.0F;
    public static float containerPotionTextOpacity = 1.0F;

    // Recipe Book Variables
    public static float recipeBookToggleOpacity = 1.0F;
    public static float recipeSlotOpacity = 1.0F;
    public static float recipeBookSearchOpacity = 1.0F;

    // HUD Variables
    public static float hudOpacity = 1.0F;
    public static float hotbarSelectionOpacity = 1.0F;
    public static float hudItemCountOpacity = 1.0F;

    // HUD Potion Variables
    public static float hudPotionIconOpacity = 1.0F;
    public static float hudPotionBackgroundOpacity = 1.0F;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                isEnabled = data.isEnabled;

                transparency = Math.max(0.0F, Math.min(1.0F, data.transparency));
                slotHighlightOpacity = Math.max(0.0F, Math.min(1.0F, data.slotHighlightOpacity));
                containerItemCountOpacity = Math.max(0.0F, Math.min(1.0F, data.containerItemCountOpacity));

                containerPotionIconOpacity = Math.max(0.0F, Math.min(1.0F, data.containerPotionIconOpacity));
                containerPotionBackgroundOpacity = Math.max(0.0F, Math.min(1.0F, data.containerPotionBackgroundOpacity));
                containerPotionTextOpacity = Math.max(0.0F, Math.min(1.0F, data.containerPotionTextOpacity));

                recipeBookToggleOpacity = Math.max(0.0F, Math.min(1.0F, data.recipeBookToggleOpacity));
                recipeSlotOpacity = Math.max(0.0F, Math.min(1.0F, data.recipeSlotOpacity));
                recipeBookSearchOpacity = Math.max(0.0F, Math.min(1.0F, data.recipeBookSearchOpacity));

                hudOpacity = Math.max(0.0F, Math.min(1.0F, data.hudOpacity));
                hotbarSelectionOpacity = Math.max(0.0F, Math.min(1.0F, data.hotbarSelectionOpacity));
                hudItemCountOpacity = Math.max(0.0F, Math.min(1.0F, data.hudItemCountOpacity));

                hudPotionIconOpacity = Math.max(0.0F, Math.min(1.0F, data.hudPotionIconOpacity));
                hudPotionBackgroundOpacity = Math.max(0.0F, Math.min(1.0F, data.hudPotionBackgroundOpacity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();

            data.isEnabled = isEnabled;

            data.transparency = transparency;
            data.slotHighlightOpacity = slotHighlightOpacity;
            data.containerItemCountOpacity = containerItemCountOpacity;

            data.containerPotionIconOpacity = containerPotionIconOpacity;
            data.containerPotionBackgroundOpacity = containerPotionBackgroundOpacity;
            data.containerPotionTextOpacity = containerPotionTextOpacity;

            data.recipeBookToggleOpacity = recipeBookToggleOpacity;
            data.recipeSlotOpacity = recipeSlotOpacity;
            data.recipeBookSearchOpacity = recipeBookSearchOpacity;

            data.hudOpacity = hudOpacity;
            data.hotbarSelectionOpacity = hotbarSelectionOpacity;
            data.hudItemCountOpacity = hudItemCountOpacity;

            data.hudPotionIconOpacity = hudPotionIconOpacity;
            data.hudPotionBackgroundOpacity = hudPotionBackgroundOpacity;

            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        boolean isEnabled = true;

        float transparency = 1.0F;
        float slotHighlightOpacity = 1.0F;
        float containerItemCountOpacity = 1.0F;

        float containerPotionIconOpacity = 1.0F;
        float containerPotionBackgroundOpacity = 1.0F;
        float containerPotionTextOpacity = 1.0F;

        float recipeBookToggleOpacity = 1.0F;
        float recipeSlotOpacity = 1.0F;
        float recipeBookSearchOpacity = 1.0F;

        float hudOpacity = 1.0F;
        float hotbarSelectionOpacity = 1.0F;
        float hudItemCountOpacity = 1.0F;

        float hudPotionIconOpacity = 1.0F;
        float hudPotionBackgroundOpacity = 1.0F;
    }
}