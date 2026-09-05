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

    public static boolean isEnabled = true;
    public static float transparency = 1.0F;
    public static float slotHighlightOpacity = 1.0F;
    public static float containerTextBrightness = 0.5F;

    public static float containerPotionIconOpacity = 1.0F;
    public static float containerPotionBackgroundOpacity = 1.0F;
    public static float containerPotionTextOpacity = 1.0F;

    public static float hudPotionIconOpacity = 1.0F;
    public static float hudPotionBackgroundOpacity = 1.0F;

    public static float recipeBookToggleOpacity = 1.0F;
    public static float recipeSlotOpacity = 1.0F;

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
                containerTextBrightness = Math.max(0.0F, Math.min(1.0F, data.containerTextBrightness));

                containerPotionIconOpacity = Math.max(0.0F, Math.min(1.0F, data.containerPotionIconOpacity));
                containerPotionBackgroundOpacity = Math.max(0.0F, Math.min(1.0F, data.containerPotionBackgroundOpacity));
                containerPotionTextOpacity = Math.max(0.0F, Math.min(1.0F, data.containerPotionTextOpacity));

                hudPotionIconOpacity = Math.max(0.0F, Math.min(1.0F, data.hudPotionIconOpacity));
                hudPotionBackgroundOpacity = Math.max(0.0F, Math.min(1.0F, data.hudPotionBackgroundOpacity));

                recipeBookToggleOpacity = Math.max(0.0F, Math.min(1.0F, data.recipeBookToggleOpacity));
                recipeSlotOpacity = Math.max(0.0F, Math.min(1.0F, data.recipeSlotOpacity));
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
            data.containerTextBrightness = containerTextBrightness;
            data.containerPotionIconOpacity = containerPotionIconOpacity;
            data.containerPotionBackgroundOpacity = containerPotionBackgroundOpacity;
            data.containerPotionTextOpacity = containerPotionTextOpacity;
            data.hudPotionIconOpacity = hudPotionIconOpacity;
            data.hudPotionBackgroundOpacity = hudPotionBackgroundOpacity;
            data.recipeBookToggleOpacity = recipeBookToggleOpacity;
            data.recipeSlotOpacity = recipeSlotOpacity;
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        transparency = 1.0F;
        slotHighlightOpacity = 1.0F;
        containerTextBrightness = 0.5F;
        containerPotionIconOpacity = 1.0F;
        containerPotionBackgroundOpacity = 1.0F;
        containerPotionTextOpacity = 1.0F;
        hudPotionIconOpacity = 1.0F;
        hudPotionBackgroundOpacity = 1.0F;
        recipeBookToggleOpacity = 1.0F;
        recipeSlotOpacity = 1.0F;
        save();
    }

    private static class ConfigData {
        boolean isEnabled = true;
        float transparency = 1.0F;
        float slotHighlightOpacity = 1.0F;
        float containerTextBrightness = 0.5F;
        float containerPotionIconOpacity = 1.0F;
        float containerPotionBackgroundOpacity = 1.0F;
        float containerPotionTextOpacity = 1.0F;
        float hudPotionIconOpacity = 1.0F;
        float hudPotionBackgroundOpacity = 1.0F;
        float recipeBookToggleOpacity = 1.0F;
        float recipeSlotOpacity = 1.0F;
    }
}