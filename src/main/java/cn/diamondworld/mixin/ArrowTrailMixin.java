package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowTrailMixin {
 @Unique private Vec3d diamondworld$beforeTick;
 @Unique private int diamondworld$oreCount;
 @Inject(method="tick",at=@At("HEAD"))
 private void diamondworld$remember(CallbackInfo ci) {diamondworld$beforeTick=((PersistentProjectileEntity)(Object)this).getEntityPos();}
 @Inject(method="tick",at=@At("RETURN"))
 private void diamondworld$leaveOre(CallbackInfo ci) {
  var arrow=(PersistentProjectileEntity)(Object)this;
  if(diamondworld$beforeTick!=null && diamondworld$oreCount<40)
   diamondworld$oreCount+=HostileTraits.arrowTrail(arrow,diamondworld$beforeTick,arrow.getEntityPos(),40-diamondworld$oreCount);
 }
 @Inject(method="onBlockHit",at=@At("TAIL"))
 private void diamondworld$impact(BlockHitResult hit,CallbackInfo ci) {HostileTraits.arrowImpact((PersistentProjectileEntity)(Object)this,hit.getBlockPos());}
}
