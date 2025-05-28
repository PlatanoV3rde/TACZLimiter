package com.platanov3rde.taczlimiter.config;

import net.minecraft.resources.ResourceLocation;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

public class ModConfig {
    public static final Set<String> DISABLED_WORLDS = new HashSet<>();
    private static final String CONFIG_FILE = "./config/taczlimiter-config.yml";
    private static final List<String> DEFAULT_WORLDS = List.of(
        "minecraft:the_nether",
        "minecraft:the_end"
    );

    public static void load() {
        try {
            File configFile = new File(CONFIG_FILE);
            createDefaultConfigIfMissing(configFile);

            Yaml yaml = new Yaml();
            try (FileReader reader = new FileReader(configFile)) {
                Map<String, Object> data = yaml.load(reader);
                if (data != null && data.containsKey("disabled_worlds")) {
                    List<String> worlds = (List<String>) data.get("disabled_worlds");
                    DISABLED_WORLDS.clear();
                    DISABLED_WORLDS.addAll(validateWorldNames(worlds));
                }
            }
        } catch (Exception e) {
            System.err.println("[TACZLimiter] Error loading config, using defaults: " + e.getMessage());
            DISABLED_WORLDS.addAll(DEFAULT_WORLDS);
        }
    }

    private static void createDefaultConfigIfMissing(File configFile) throws Exception {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("# TACZ Limiter Configuration\n");
                writer.write("disabled_worlds:\n");
                DEFAULT_WORLDS.forEach(world -> writer.write("  - " + world + "\n"));
            }
        }
    }

    private static Set<String> validateWorldNames(List<String> worldNames) {
        Set<String> validNames = new HashSet<>();
        if (worldNames != null) {
            for (String name : worldNames) {
                try {
                    // Validate format without creating ResourceLocation
                    if (name.matches("^[a-z0-9_.-]+:[a-z0-9_.-]+$")) {
                        validNames.add(name.toLowerCase());
                    }
                } catch (Exception e) {
                    System.err.println("Invalid world name in config: " + name);
                }
            }
        }
        return validNames.isEmpty() ? new HashSet<>(DEFAULT_WORLDS) : validNames;
    }
}
