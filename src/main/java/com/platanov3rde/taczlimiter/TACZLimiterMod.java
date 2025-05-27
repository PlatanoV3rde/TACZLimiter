package com.platanov3rde.taczlimiter;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mod("taczlimiter")
public class TACZLimiterMod {
    private static Set<ResourceLocation> disabledWorlds = Collections.emptySet();

    public TACZLimiterMod() {
        loadConfig();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void loadConfig() {
        File configFile = new File("config/tacz_limiter_config.yml");
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write("disabled_worlds:\n" +
                            "  - minecraft:overworld\n" +
                            "  - minecraft:the_nether\n" +
                            "  - minecraft:the_end\n");
                }
            }

            Yaml yaml = new Yaml();
            var data = yaml.load(new FileReader(configFile));
            if (data instanceof java.util.Map<?, ?> map) {
                Object worldsObj = map.get("disabled_worlds");
                if (worldsObj instanceof List<?> list) {
                    disabledWorlds = ImmutableSet.copyOf(
                        list.stream()
                            .map(Object::toString)
                            .map(ResourceLocation::new)
                            .toList()
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("[TACZLimiter] Error cargando configuración: " + e.getMessage());
            disabledWorlds = ImmutableSet.of(
                new ResourceLocation("minecraft:the_nether"),
                new ResourceLocation("minecraft:the_end")
            );
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;

        Level level = event.getLevel();
        ResourceLocation worldId = level.dimension().location();

        if (!disabledWorlds.contains(worldId)) return;

        ItemStack itemStack = event.getItemStack();
        ResourceLocation itemId = itemStack.getItem().getRegistryName();
        
        // Verifica si el item es de TACZ
        if (itemId != null && itemId.getNamespace().equals("tacz")) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(
                net.minecraft.network.chat.Component.literal("§c¡No puedes usar armas TACZ en este mundo!")
            );
            
            // Opcional: Hacer que el jugador suelte el arma
            // event.getEntity().drop(itemStack.copy(), false);
            // itemStack.setCount(0);
        }
    }
}
