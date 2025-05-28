package com.platanov3rde.taczlimiter;

import com.google.common.collect.ImmutableSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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

    // Bloquear disparo (click izquierdo) con el arma TACZ
    @SubscribeEvent
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getLevel().isClientSide()) return;

        // Solo main hand
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        // Filtrar solo clicks izquierdos (en aire o bloque)
        PlayerInteractEvent.Action action = event.getAction();
        if (action != PlayerInteractEvent.Action.LEFT_CLICK_AIR && action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getEntity();
        Level world = event.getLevel();
        String worldName = world.dimension().location().getPath();

        if (!disabledWorlds.contains(worldName)) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ResourceLocation mainId = ForgeRegistries.ITEMS.getKey(mainHand.getItem());
        ResourceLocation offId = ForgeRegistries.ITEMS.getKey(offHand.getItem());

        boolean isTACZGun = (mainId != null && mainId.equals(TACZ_GUN_ID)) ||
                            (offId != null && offId.equals(TACZ_GUN_ID));

        if (isTACZGun) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c¡No puedes disparar esta arma en esta zona!"));
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    // Bloquear daño causado con el arma TACZ - respaldo
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        Level world = player.level();
        String worldName = world.dimension().location().getPath();

        if (!disabledWorlds.contains(worldName)) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ResourceLocation mainId = ForgeRegistries.ITEMS.getKey(mainHand.getItem());
        ResourceLocation offId = ForgeRegistries.ITEMS.getKey(offHand.getItem());

        boolean isTACZGun = (mainId != null && mainId.equals(TACZ_GUN_ID)) ||
                            (offId != null && offId.equals(TACZ_GUN_ID));

        if (isTACZGun) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c¡No puedes usar esta arma en este mundo!"));
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
