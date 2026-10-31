package com.susu.orechests.mixin;

import com.susu.orechests.OreChestsMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballMixin {
    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void orechests$applySnare(EntityHitResult hit, CallbackInfo ci) {
        Snowball snowball = (Snowball) (Object) this;
        ItemStack thrownStack = snowball.getItem();

        if (OreChestsMod.isNamed(thrownStack, OreChestsMod.BLACK_BALL_NAME)
                && hit.getEntity() instanceof LivingEntity living
                && !living.level().isClientSide()) {
            // Slowness X for 10 seconds; effectively prevents ordinary movement.
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 9));
        }
    }
}
