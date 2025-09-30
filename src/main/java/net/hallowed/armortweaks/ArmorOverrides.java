package net.hallowed.armortweaks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ArmorOverrides {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("armortweaks-armor.json");

    private static final Map<ResourceLocation, Pair> CACHE = new HashMap<>();
    public record Pair(double armor, double toughness) {}

    public static synchronized void load() {
        CACHE.clear();
        try {
            if (!Files.exists(FILE)) {
                String starter = """
                [
                  "minecraft:diamond_helmet,3,2",
                  "minecraft:diamond_chestplate,8,2",
                  "minecraft:diamond_leggings,6,2",
                  "minecraft:diamond_boots,3,2"
                ]
                """;
                Files.writeString(FILE, starter, StandardCharsets.UTF_8);
                LOGGER.info("[ArmorTweaks] Wrote starter file: {}", FILE);
                return;
            }

            String json = Files.readString(FILE, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) return;

            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            JsonElement root = JsonParser.parseReader(reader);
            if (root == null || !root.isJsonArray()) return;

            JsonArray arr = root.getAsJsonArray();
            for (JsonElement el : arr) {
                if (!el.isJsonPrimitive() || !el.getAsJsonPrimitive().isString()) continue;

                String raw = el.getAsString();
                for (String piece : raw.split(";")) {
                    String line = piece.trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split(",");
                    if (parts.length < 3) continue;

                    String idStr = parts[0].trim();
                    Double armor = tryParse(parts[1].trim());
                    Double tough = tryParse(parts[2].trim());
                    if (idStr.isEmpty() || armor == null || tough == null) continue;

                    ResourceLocation id = ResourceLocation.tryParse(idStr);
                    if (id == null) continue;

                    CACHE.put(id, new Pair(armor, tough));
                }
            }

            LOGGER.info("[ArmorTweaks] Loaded {} armor override(s) from {}", CACHE.size(), FILE);
        } catch (Exception e) {
            LOGGER.warn("[ArmorTweaks] Failed to load {}: {}", FILE, e.toString());
        }
    }

    public static synchronized Optional<Pair> get(ResourceLocation id) {
        return Optional.ofNullable(CACHE.get(id));
    }

    private static Double tryParse(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }

    public static synchronized int size() {
        return CACHE.size();
    }

    private ArmorOverrides() {}
}
