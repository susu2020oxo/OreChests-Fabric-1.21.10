package cn.diamondworld.mixin;

import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(TridentEntity.class)
public abstract class TridentDamageMixin {
    @ModifyArg(method="onEntityHit",at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),index=1)
    private float diamondworld$thrownDamage(float damage) {return damage+4F;}
}
