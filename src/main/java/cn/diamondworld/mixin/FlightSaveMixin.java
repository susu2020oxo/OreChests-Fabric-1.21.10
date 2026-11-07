package cn.diamondworld.mixin;
import cn.diamondworld.EssenceAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(PlayerEntity.class)
public abstract class FlightSaveMixin {
    @Unique private PlayerAbilities.Packed diamondworld$liveAbilities;
    @Inject(method="writeCustomData",at=@At("HEAD"))
    private void diamondworld$saveOriginalPermissions(CallbackInfo ci) {
        diamondworld$liveAbilities=EssenceAbilities.beforeSave((PlayerEntity)(Object)this);
    }
    @Inject(method="writeCustomData",at=@At("RETURN"))
    private void diamondworld$restoreLiveAbilities(CallbackInfo ci) {
        if(diamondworld$liveAbilities!=null) {
            ((PlayerEntity)(Object)this).getAbilities().unpack(diamondworld$liveAbilities);
            diamondworld$liveAbilities=null;
        }
    }
}
