package cn.diamondworld.mixin;
import cn.diamondworld.EssenceAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(PlayerEntity.class)
public abstract class PlayerClimbingMixin {
    @Inject(method="isClimbing",at=@At("HEAD"),cancellable=true)
    private void diamondworld$spiderGrip(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player=(PlayerEntity)(Object)this;
        if(!player.isSpectator() && player.horizontalCollision && player.hasStatusEffect(EssenceAbilities.CLIMBING))cir.setReturnValue(true);
    }
}
