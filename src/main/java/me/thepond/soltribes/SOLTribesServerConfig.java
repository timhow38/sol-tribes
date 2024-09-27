package me.thepond.soltribes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SOLTribesServerConfig {

    public static final String ACTIVATION_COST = "tribe_activation_cost";
    public static final String FLAG_EFFECT_DISTANCE = "tribe_flag_effect_distance";
    public static final String FLAG_MAX_CAPACITY = "tribe_flag_max_capacity";
    public static final String FLAG_CAPACITY_SCALING = "tribe_flag_capacity_scaling";

    public static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("soltribes.server.json");
    public static JsonObject CONFIG_OBJECT = new JsonObject();

    public static void writeToBuf(PacketByteBuf buf) {
        buf.writeString(GSON.toJson(CONFIG_OBJECT));
    }

    public static void readFromString(String bufString) {
        CONFIG_OBJECT = GSON.fromJson(bufString, JsonObject.class);
    }

    public static float getFloat(String option) {
        return getFloat(option, 1f);
    }

    public static float getFloat(String option, float defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsFloat();
    }

    public static void setFloat(String option, float value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static boolean getBoolean(String option) {
        return getBoolean(option, false);
    }

    public static boolean getBoolean(String option, boolean defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsBoolean();
    }

    public static void setBoolean(String option, boolean value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static int getInt(String option) {
        return getInt(option, 0);
    }

    public static int getInt(String option, int defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsInt();
    }

    public static double getDouble(String option) {
        return getDouble(option, 0);
    }

    public static double getDouble(String option, double defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsDouble();
    }

    public static void setInt(String option, int value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static String getString(String option) {
        return getString(option, "");
    }

    public static String getString(String option, String defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsString();
    }

    public static void setString(String option, String value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static void generateDefaultOptions() {
        CONFIG_OBJECT.addProperty(ACTIVATION_COST, 300);
        CONFIG_OBJECT.addProperty(FLAG_EFFECT_DISTANCE, 200D);
        CONFIG_OBJECT.addProperty(FLAG_MAX_CAPACITY, 5);
        CONFIG_OBJECT.addProperty(FLAG_CAPACITY_SCALING, 300);
    }

    public static void loadOptions() {
        try {
            if (CONFIG_FILE.toFile().exists())
            {
                CONFIG_OBJECT = GSON.fromJson(Files.readString(CONFIG_FILE, StandardCharsets.UTF_8), JsonObject.class);
            } else {
                generateDefaultOptions();
                saveOptions();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveOptions() {
        try {
            Files.writeString(CONFIG_FILE, GSON.toJson(CONFIG_OBJECT), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
