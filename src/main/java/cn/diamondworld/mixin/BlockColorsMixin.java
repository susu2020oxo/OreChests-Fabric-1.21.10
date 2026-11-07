package cn.diamondworld.mixin;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
    @Inject(method="getColor", at=@At("HEAD"), cancellable=true)
    private void diamondworld$neutralTint(CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(0xFFFFFFFF); }
}
