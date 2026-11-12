package cn.diamondworld.mixin;

import net.minecraft.item.Item;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Complements material bonuses for vanilla equipment that does not use ToolMaterial/ArmorMaterial. */
@Mixin(Item.class)
public abstract class UtilityGearMixin {
    @Shadow @Final @Mutable private ComponentMap components;
    @Shadow @Final protected String translationKey;
    @Inject(method="<init>",at=@At("RETURN"))
    private void diamondworld$specialGear(Item.Settings settings,CallbackInfo ci) {
        if(!translationKey.startsWith("item.minecraft."))return;
        String id=translationKey.substring("item.minecraft.".length());
        Integer maximum=components.get(DataComponentTypes.MAX_DAMAGE);
        if(maximum==null)return;
        // These items have already had durability doubled in their material constructor.
        boolean materialTool=id.matches("(wooden|stone|copper|iron|golden|diamond|netherite)_(sword|pickaxe|axe|shovel|hoe)");
        AttributeModifiersComponent attributes=components.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,AttributeModifiersComponent.DEFAULT);
        boolean materialArmor=attributes.modifiers().stream().anyMatch(e->e.attribute().equals(EntityAttributes.ARMOR));
        if(materialTool || materialArmor)return;
        var builder=ComponentMap.builder().addAll(components)
            .add(DataComponentTypes.MAX_DAMAGE,Math.max(1,maximum*2));
        if(id.equals("trident") || id.equals("mace") || id.equals("shears")) {
            attributes=attributes.with(EntityAttributes.ATTACK_DAMAGE,
                new EntityAttributeModifier(Identifier.of("diamondworld","special_weapon_damage"),4,
                    EntityAttributeModifier.Operation.ADD_VALUE),AttributeModifierSlot.MAINHAND);
            builder.add(DataComponentTypes.ATTRIBUTE_MODIFIERS,attributes);
        }
        if(id.equals("shield") || id.equals("elytra")) {
            AttributeModifierSlot slot=id.equals("shield")?AttributeModifierSlot.OFFHAND:AttributeModifierSlot.CHEST;
            attributes=attributes.with(EntityAttributes.ARMOR,
                new EntityAttributeModifier(Identifier.of("diamondworld",id+"_defense"),3,
                    EntityAttributeModifier.Operation.ADD_VALUE),slot);
            attributes=attributes.with(EntityAttributes.ARMOR_TOUGHNESS,
                new EntityAttributeModifier(Identifier.of("diamondworld",id+"_toughness"),1,
                    EntityAttributeModifier.Operation.ADD_VALUE),slot);
            builder.add(DataComponentTypes.ATTRIBUTE_MODIFIERS,attributes);
        }
        // Preserve each special tool's effective-block rules and harvesting permissions.
        ToolComponent tool=components.get(DataComponentTypes.TOOL);
        if(tool!=null) {
            var rules=tool.rules().stream().map(r->new ToolComponent.Rule(r.blocks(),r.speed().map(speed->speed+3F),r.correctForDrops())).toList();
            builder.add(DataComponentTypes.TOOL,new ToolComponent(rules,tool.defaultMiningSpeed()+3F,tool.damagePerBlock(),tool.canDestroyBlocksInCreative()));
        }
        components=builder.build();
    }
}
