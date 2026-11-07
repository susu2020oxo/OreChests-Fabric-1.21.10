package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(PotionEntity.class)
public abstract class WitchPotionMixin {
 @Inject(method="onCollision",at=@At("TAIL"))
 private void diamondworld$crystalSplash(HitResult hit,CallbackInfo ci) {
  HostileTraits.witchSplash((PotionEntity)(Object)this,hit.getPos());
 }
}
