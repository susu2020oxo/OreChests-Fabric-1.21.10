package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(EvokerFangsEntity.class)
public abstract class EvokerFangsMixin {
 @Inject(method="damage(Lnet/minecraft/entity/LivingEntity;)V",at=@At("TAIL"))
 private void diamondworld$fangOre(LivingEntity target,CallbackInfo ci) {HostileTraits.evokerFang((EvokerFangsEntity)(Object)this);}
}
