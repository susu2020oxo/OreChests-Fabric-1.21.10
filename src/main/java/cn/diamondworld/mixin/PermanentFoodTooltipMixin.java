package cn.diamondworld.mixin;

import java.util.function.Consumer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class PermanentFoodTooltipMixin {
    @Inject(method="appendTooltip",at=@At("TAIL"))
    private void diamondworld$showFoodGrowth(ItemStack stack, Item.TooltipContext context,
            TooltipDisplayComponent display, Consumer<Text> lines, TooltipType type, CallbackInfo ci) {
        if (stack.contains(DataComponentTypes.FOOD))
            lines.accept(Text.literal("食用后永久生命上限 +1（半颗心）").formatted(Formatting.LIGHT_PURPLE));
        else if (stack.isOf(Items.CAKE))
            lines.accept(Text.literal("放置后每吃一口，永久生命上限 +1").formatted(Formatting.LIGHT_PURPLE));
    }
}
