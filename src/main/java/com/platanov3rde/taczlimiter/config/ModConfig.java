package com.ejemplo.taczlimiter.config;

import net.minecraft.resources.ResourceLocation;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.Yaml;

public class ModConfig {
    public static List<ResourceLocation> DISABLED_WORLDS = new ArrayList<>();

    public static void load() {
        File configDir = new File("./config");
        if (!configDir.exists()) configDir.mkdirs();
        
        File configFile = new File(configDir, "taczlimiter-config.yml");
        Yaml yaml = new Yaml();
        
        try (FileReader reader = new FileReader(configFile)) {
            Map<String, Object> data = yaml.load(reader);
            List<String> worlds = (List<String>) data.get("disabled_worlds");
            if (worlds != null) {
                DISABLED_WORLDS.clear();
                worlds.forEach(world -> DISABLED_WORLDS.add(new ResourceLocation(world)));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar config.yml, usando valores por defecto.");
            DISABLED_WORLDS.add(new ResourceLocation("minecraft:the_nether")); // Ejemplo por defecto
        }
    }
}
