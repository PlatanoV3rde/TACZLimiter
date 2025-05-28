package com.platanov3rde.taczlimiter.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        "minecraft:the_end",
        "spawn",
        "lobby"
    );

    public static void load() {
        try {
            File configFile = new File(CONFIG_FILE);
            createDefaultConfigIfMissing(configFile);
            loadConfigFile(configFile);
        } catch (Exception e) {
            System.err.println("[TACZLimiter] Error loading config, using defaults: " + e.getMessage());
            DISABLED_WORLDS.addAll(DEFAULT_WORLDS);
        }
    }

    private static void createDefaultConfigIfMissing(File configFile) throws IOException {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("# TACZ Limiter Configuration\n");
                writer.write("disabled_worlds:\n");
                for (String world : DEFAULT_WORLDS) {
                    writer.write("  - " + world + "\n");
                }
            }
        }
    }

    private static void loadConfigFile(File configFile) throws IOException {
        Yaml yaml = new Yaml();
        try (FileReader reader = new FileReader(configFile)) {
            Map<String, Object> data = yaml.load(reader);
            if (data != null && data.containsKey("disabled_worlds")) {
                DISABLED_WORLDS.clear();
                @SuppressWarnings("unchecked")
                List<String> worlds = (List<String>) data.get("disabled_worlds");
                worlds.forEach(world -> DISABLED_WORLDS.add(world.toLowerCase()));
            }
        }
    }
}
