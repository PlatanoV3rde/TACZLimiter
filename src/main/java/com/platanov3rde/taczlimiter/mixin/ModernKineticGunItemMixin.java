package com.platanov3rde.taczlimiter.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Reemplaza 'ModernKineticGunItem' con la clase real del ítem en TaCZ
@Mixin(targets = "tacz.item.ModernKineticGunItem")
public class ModernKineticGunItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        // Lista de mundos restringidos
        String[] mundosRestringidos = {"minecraft:the_nether", "custom:ascenders_survival"};

        ResourceLocation dimension = level.dimension().location();
        for (String mundo : mundosRestringidos) {
            if (dimension.toString().equals(mundo)) {
                if (!level.isClientSide) {
                    player.sendSystemMessage(Component.literal("§c¡No puedes usar armas en este mundo!"));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                cir.setReturnValue(InteractionResultHolder.fail(player.getItemInHand(hand)));
                return;
            }
        }
    }
}
