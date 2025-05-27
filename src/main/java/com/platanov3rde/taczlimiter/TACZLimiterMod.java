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
import java.util.Set;

@Mod("taczlimiter")
public class TACZLimiterMod {
    private static final ResourceLocation TACZ_GUN_ID = new ResourceLocation("tacz", "modern_kinetic_gun");
    private static Set<String> disabledWorlds = Collections.emptySet(); // Usamos nombres simples (ej: "spawn")

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
                            "  - spawn\n" +  // Mundo personalizado (nombre de carpeta)
                            "  - lobby\n" +
                            "  - minecraft:the_nether\n"); // Ejemplo con dimensión vanilla
                }
            }

            Yaml yaml = new Yaml();
            var data = yaml.load(new FileReader(configFile));
            if (data instanceof java.util.Map<?, ?> map) {
                disabledWorlds = ImmutableSet.copyOf((List<String>) map.get("disabled_worlds"));
            }
        } catch (Exception e) {
            System.err.println("[TACZLimiter] Error al cargar config.yml. Usando valores por defecto.");
            disabledWorlds = ImmutableSet.of("spawn", "lobby"); // Mundos por defecto bloqueados
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;

        Level world = event.getLevel();
        String worldName = world.dimension().location().getPath(); // Ej: "spawn" (nombre de carpeta)

        // Verifica si el mundo actual está en la lista de bloqueados
        if (!disabledWorlds.contains(worldName)) return;

        ItemStack item = event.getItemStack();
        // Comprueba si es un arma TACZ (usando el ID base)
        if (TACZ_GUN_ID.equals(item.getItem().getRegistryName())) {
            event.setCanceled(true); // Bloquea el uso del arma
            event.getEntity().sendSystemMessage(
                net.minecraft.network.chat.Component.literal("§c¡Armas TACZ desactivadas en este mundo!")
            );
            
            // Opcional: Reproducir sonido de error (ej: "item.shield.block")
            world.playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                net.minecraft.sounds.SoundEvents.SHIELD_BLOCK, 
                net.minecraft.sounds.SoundSource.PLAYERS, 
                1.0F, 1.0F);
        }
    }
}
