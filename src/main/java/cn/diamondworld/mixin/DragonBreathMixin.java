package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(DragonFireballEntity.class)
public abstract class DragonBreathMixin {
 @Inject(method="onCollision",at=@At("TAIL"))
 private void diamondworld$crystalPool(HitResult hit,CallbackInfo ci) {HostileTraits.dragonBreath((DragonFireballEntity)(Object)this,hit.getPos());}
}
