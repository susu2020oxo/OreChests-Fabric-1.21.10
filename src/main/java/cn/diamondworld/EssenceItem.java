package cn.diamondworld;
import java.util.function.Consumer;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class EssenceItem extends Item {
    private final String species;
    public EssenceItem(Settings settings,String species) {super(settings);this.species=species;}
    @Override public void appendTooltip(ItemStack stack,TooltipContext context,TooltipDisplayComponent display,Consumer<Text> tooltip,TooltipType type) {
        super.appendTooltip(stack,context,display,tooltip,type);
        for(var effect:EssenceAbilities.effects(species))
            tooltip.accept(Text.translatable(effect.getTranslationKey()).append(" "+(effect.getAmplifier()+1)+" · "+effect.getDuration()/20+"s").formatted(Formatting.AQUA));
        String description=EssenceAbilities.description(species);
        if(!description.isEmpty())tooltip.accept(Text.literal(description).formatted(Formatting.GRAY));
    }
}
