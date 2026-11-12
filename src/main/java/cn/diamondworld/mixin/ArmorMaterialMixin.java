package cn.diamondworld.mixin;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Every armor tier, including horse/wolf body armor, receives the same additive defense bonus. */
@Mixin(ArmorMaterial.class)
public abstract class ArmorMaterialMixin {
    @Shadow @Final @Mutable private int durability;
    @Shadow @Final @Mutable private Map<EquipmentType,Integer> defense;
    @Shadow @Final @Mutable private float toughness;
    @Inject(method="<init>",at=@At("RETURN"))
    private void diamondworld$reinforce(CallbackInfo ci) {
        durability=Math.max(1,durability*2);
        Map<EquipmentType,Integer> enhanced=new EnumMap<>(EquipmentType.class);
        defense.forEach((type,value)->enhanced.put(type,value+3));
        defense=Map.copyOf(enhanced);
        toughness+=1.0F;
    }
}
