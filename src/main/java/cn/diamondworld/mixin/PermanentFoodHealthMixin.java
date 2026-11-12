package cn.diamondworld.mixin;

import cn.diamondworld.PermanentHealth;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** FoodComponent runs once per actual meal, before the last stack item is decremented. */
@Mixin(FoodComponent.class)
public abstract class PermanentFoodHealthMixin {
    @Inject(method="onConsume", at=@At("TAIL"))
    private void diamondworld$permanentFoodHealth(World world, LivingEntity user, ItemStack stack,
                                                 ConsumableComponent consumable, CallbackInfo ci) {
        if (user instanceof ServerPlayerEntity player && !stack.isEmpty()) PermanentHealth.eat(player);
    }
}
