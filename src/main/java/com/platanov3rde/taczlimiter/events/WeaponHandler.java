package com.platanov3rde.taczlimiter.events;

import com.platanov3rde.taczlimiter.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class WeaponHandler {
    @SubscribeEvent
    public static void onPlayerShoot(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        
        if (isTACZWeapon(heldItem)) {
            ResourceLocation worldId = player.level().dimension().location();
            
            if (ModConfig.DISABLED_WORLDS.contains(worldId.toString())) {
                event.setCanceled(true);
                player.displayClientMessage(
                    Component.literal("¡No puedes usar armas de TACZ en este mundo!"),
                    true
                );
                
                // Opción 2: Soltar el arma al suelo (actualizada)
                if (!player.getAbilities().instabuild) {
                    player.drop(heldItem.copy(), false, false); // Los parámetros extra evitan el sonido de pickup
                    heldItem.setCount(0);
                }
            }
        }
    }
    
    private static boolean isTACZWeapon(ItemStack item) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item.getItem());
        return itemId != null && itemId.getNamespace().equals("tacz");
    }
}
