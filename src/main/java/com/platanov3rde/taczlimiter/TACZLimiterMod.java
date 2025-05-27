package com.platanov3rde.taczlimiter;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mod("taczlimiter")
public class TACZLimiterMod {

    private static Set<String> disabledWorlds = Collections.emptySet();

    public TACZLimiterMod() {
        loadConfig();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void loadConfig() {
        File configFile = new File("config/tacz_limiter_config.yml");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                java.nio.file.Files.writeString(configFile.toPath(),
                        "disabled_worlds:\n  - world\n  - lobby\n");
            } catch (Exception e) {
                System.err.println("[TACZLimiter] No se pudo crear el archivo de configuración.");
            }
        }

        try {
            Yaml yaml = new Yaml();
            var data = yaml.load(new FileReader(configFile));
            if (data instanceof java.util.Map map) {
                Object worldsObj = map.get("disabled_worlds");
                if (worldsObj instanceof List list) {
                    disabledWorlds = ImmutableSet.copyOf(list.stream().map(Object::toString).toList());
                }
            }
        } catch (Exception e) {
            System.err.println("[TACZLimiter] Error cargando el config.yml: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Level level = event.getLevel();
        String worldName = level.dimension().location().getPath();

        if (!disabledWorlds.contains(worldName)) return;

        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem().getClass().getName().startsWith("tacz.")) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(
                net.minecraft.network.chat.Component.literal("§c¡No puedes usar armas TACZ en este mundo!"));
        }
    }
}
