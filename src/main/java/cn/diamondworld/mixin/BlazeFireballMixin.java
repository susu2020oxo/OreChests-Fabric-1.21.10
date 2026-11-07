package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(SmallFireballEntity.class)
public abstract class BlazeFireballMixin {
 @Inject(method="onBlockHit",at=@At("TAIL"))
 private void diamondworld$impact(BlockHitResult hit,CallbackInfo ci) {HostileTraits.fireballImpact((SmallFireballEntity)(Object)this,hit.getBlockPos());}
}
