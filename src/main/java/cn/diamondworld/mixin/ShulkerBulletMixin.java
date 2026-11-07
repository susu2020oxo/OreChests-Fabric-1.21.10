package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ShulkerBulletEntity.class)
public abstract class ShulkerBulletMixin {
 @Inject(method="onCollision",at=@At("TAIL"))
 private void diamondworld$crystalImpact(HitResult hit,CallbackInfo ci) {HostileTraits.shulkerImpact((ShulkerBulletEntity)(Object)this,hit.getPos());}
}
