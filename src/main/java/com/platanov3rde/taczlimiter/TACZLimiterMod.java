package com.platanov3rde.taczlimiter;

import com.google.common.collect.ImmutableSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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
    public void onLivingAttack(LivingAttackEvent event) {
        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof Player player)) return;
        Level world = player.level();
        String worldName = world.dimension().location().getPath();

        if (!disabledWorlds.contains(worldName)) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ResourceLocation mainItemId = ForgeRegistries.ITEMS.getKey(mainHand.getItem());
        ResourceLocation offItemId = ForgeRegistries.ITEMS.getKey(offHand.getItem());

        boolean isTACZGun = (mainItemId != null && mainItemId.equals(TACZ_GUN_ID)) ||
                            (offItemId != null && offItemId.equals(TACZ_GUN_ID));

        if (isTACZGun) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c¡No puedes usar esta arma en este mundo!"));

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
