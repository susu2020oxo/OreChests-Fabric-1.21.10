package cn.diamondworld.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/** Add four hit points after velocity/critical/enchantment calculation, rather than multiplying a coefficient. */
@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowDamageMixin {
    @ModifyArg(method="onEntityHit",at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),index=1)
    private float diamondworld$bowDamage(float damage) {
        var weapon=((PersistentProjectileEntity)(Object)this).getWeaponStack();
        return weapon!=null && (weapon.isOf(Items.BOW)||weapon.isOf(Items.CROSSBOW))?damage+4F:damage;
    }
}
