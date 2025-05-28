package com.platanov3rde.taczlimiter;

import com.google.common.collect.ImmutableSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod("taczlimiter")
public class TACZLimiterMod {
    private static final ResourceLocation TACZ_GUN_ID = new ResourceLocation("tacz", "modern_kinetic_gun");
    private static Set<String> disabledWorlds = Collections.emptySet();

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
                            "  - spawn\n" +
                            "  - lobby\n" +
                            "  - minecraft:the_nether\n");
                }
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new FileReader(configFile));
            if (data != null) {
                disabledWorlds = ImmutableSet.copyOf((List<String>) data.get("disabled_worlds"));
            }
        } catch (Exception e) {
            System.err.println("[TACZLimiter] Error al cargar config.yml. Usando valores por defecto.");
            disabledWorlds = ImmutableSet.of("spawn", "lobby");
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;

        Level world = event.getLevel();
        ResourceLocation worldId = world.dimension().location();
        String worldName = worldId.getPath();

        if (!disabledWorlds.contains(worldName) return;

        ItemStack item = event.getItemStack();
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item.getItem());

        if (itemId != null && itemId.equals(TACZ_GUN_ID)) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(
                Component.literal("§c¡Armas TACZ desactivadas en este mundo!")
            );
            
            world.playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                SoundEvents.SHIELD_BLOCK, 
                SoundSource.PLAYERS, 
                1.0F, 1.0F);
        }
    }
}
