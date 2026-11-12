package cn.diamondworld.mixin;

import cn.diamondworld.PermanentHealth;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Placed cakes have no item-consumption component, so successful slices use their own hook. */
@Mixin(CakeBlock.class)
public abstract class PermanentCakeHealthMixin {
    @Inject(method="tryEat", at=@At("RETURN"))
    private static void diamondworld$permanentCakeHealth(WorldAccess world, BlockPos pos, BlockState state,
                                                        PlayerEntity user, CallbackInfoReturnable<ActionResult> cir) {
        if (user instanceof ServerPlayerEntity player && cir.getReturnValue().isAccepted()) PermanentHealth.eat(player);
    }
}
