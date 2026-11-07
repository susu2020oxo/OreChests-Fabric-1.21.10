package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(MobEntity.class)
public abstract class HostileMobMixin {
 @Inject(method="tick",at=@At("TAIL"))
 private void diamondworld$traits(CallbackInfo ci) {HostileTraits.onMobTick((MobEntity)(Object)this);}
}
