package com.ejemplo.taczlimiter.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WeaponHandler {
    @SubscribeEvent
    public static void onPlayerShoot(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        
        // Verifica si el item es un arma de TACZ (ajusta según el mod)
        if (isTACZWeapon(heldItem)) {
            ResourceLocation worldId = player.level().dimension().location();
            
            if (ModConfig.DISABLED_WORLDS.contains(worldId)) {
                event.setCanceled(true);
                player.displayClientMessage(
                    Component.literal("¡No puedes usar armas de TACZ en este mundo!"),
                    true
                );
                
                // Opción 1: Quitar munición (si aplica)
                // removeAmmo(player);
                
                // Opción 2: Soltar el arma al suelo
                player.drop(heldItem.copy(), false);
                heldItem.shrink(heldItem.getCount());
            }
        }
    }
    
    private static boolean isTACZWeapon(ItemStack item) {
        // Reemplaza con la lógica real para detectar armas de TACZ
        return item.getItem().getRegistryName() != null 
            && item.getItem().getRegistryName().getNamespace().equals("tacz");
    }
}
