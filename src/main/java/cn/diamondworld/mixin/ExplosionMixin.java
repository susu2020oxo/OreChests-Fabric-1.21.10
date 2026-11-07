package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ExplosionImpl.class)
public abstract class ExplosionMixin {
 @Inject(method="explode",at=@At("RETURN"))
 private void diamondworld$oreCrater(CallbackInfoReturnable<Integer> cir) {
  HostileTraits.afterExplosion((ExplosionImpl)(Object)this);
 }
}
