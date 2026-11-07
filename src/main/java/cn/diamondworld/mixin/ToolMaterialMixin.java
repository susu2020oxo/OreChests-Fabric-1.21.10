package cn.diamondworld.mixin;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Boost all vanilla tool tiers while retaining their harvest levels and recipes. */
@Mixin(ToolMaterial.class)
public abstract class ToolMaterialMixin {
    @Shadow @Final @Mutable private int durability;
    @Shadow @Final @Mutable private float speed;
    @Shadow @Final @Mutable private float attackDamageBonus;
    @Shadow @Final @Mutable private int enchantmentValue;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void diamondworld$empower(CallbackInfo ci) {
        durability = Math.max(1, durability * 2);
        speed *= 1.5F;
        attackDamageBonus += 2.0F;
        enchantmentValue += 5;
    }
}
