package com.susu.orechests.mixin;

import com.susu.orechests.OreChestsMod;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class ArrowDamageMixin {
    @Shadow
    public abstract ItemStack getWeaponItem();

    @Shadow
    public abstract void setBaseDamage(double damage);

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void orechests$setNetheriteBowDamage(EntityHitResult hit, CallbackInfo ci) {
        if (OreChestsMod.isNamed(getWeaponItem(), OreChestsMod.NETHERITE_BOW_NAME)) {
            setBaseDamage(10.0);
        }
    }
}
