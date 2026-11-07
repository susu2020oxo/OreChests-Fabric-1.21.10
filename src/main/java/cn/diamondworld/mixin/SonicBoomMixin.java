package cn.diamondworld.mixin;
import cn.diamondworld.HostileTraits;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(SonicBoomTask.class)
public abstract class SonicBoomMixin {
 @Inject(method="method_43265",at=@At("TAIL"))
 private static void diamondworld$sonicOre(WardenEntity warden,ServerWorld world,LivingEntity target,CallbackInfo ci) {HostileTraits.sonicFissure(world,warden,target);}
}
