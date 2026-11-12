package cn.diamondworld;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.item.equipment.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import org.slf4j.LoggerFactory;

/** V1.2 checks use original vanilla constants and actual damage calls. Opt-in verification only. */
public final class GearVerification {
    private static void check(boolean success,String message) {
        if(!success)throw new IllegalStateException("GEAR CHECK FAILED: "+message);
        LoggerFactory.getLogger("diamondworld").info("CHECK PASS: {}",message);
    }
    private static double attribute(Item item,RegistryEntry<EntityAttribute> attribute) {
        AttributeModifiersComponent component=item.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,AttributeModifiersComponent.DEFAULT);
        return component.modifiers().stream().filter(entry->entry.attribute().equals(attribute)
            &&entry.modifier().operation()==EntityAttributeModifier.Operation.ADD_VALUE).mapToDouble(entry->entry.modifier().value()).sum();
    }
    private static ServerPlayerEntity player(MinecraftServer server) {
        var profile=new GameProfile(UUID.randomUUID(),"GearCheck");
        var p=new ServerPlayerEntity(server,server.getOverworld(),profile,SyncedClientOptions.createDefault());
        p.networkHandler=new ServerPlayNetworkHandler(server,new ClientConnection(NetworkSide.SERVERBOUND),p,ConnectedClientData.createDefault(profile,false));
        p.refreshPositionAndAngles(2,210,2,0,0);return p;
    }
    private static float hit(MinecraftServer server,ServerPlayerEntity shooter,Item weapon,boolean thrown) throws ReflectiveOperationException {
        var world=server.getOverworld();
        var cow=EntityType.COW.create(world,SpawnReason.COMMAND);
        cow.refreshPositionAndAngles(4,210,2,0,0);
        cow.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40);
        cow.setHealth(40);
        PersistentProjectileEntity projectile=thrown?new TridentEntity(world,shooter,new ItemStack(weapon)):
            new ArrowEntity(world,shooter,new ItemStack(Items.ARROW),new ItemStack(weapon));
        projectile.setVelocity(1,0,0);
        var method=(thrown?TridentEntity.class:PersistentProjectileEntity.class).getDeclaredMethod("onEntityHit",EntityHitResult.class);
        method.setAccessible(true);
        method.invoke(projectile,new EntityHitResult(cow));
        float damage=40-cow.getHealth();projectile.discard();cow.discard();return damage;
    }
    public static void run(MinecraftServer server) {
        ToolMaterial[] materials={ToolMaterial.WOOD,ToolMaterial.STONE,ToolMaterial.COPPER,ToolMaterial.IRON,ToolMaterial.GOLD,ToolMaterial.DIAMOND,ToolMaterial.NETHERITE};
        String[] names={"wooden","stone","copper","iron","golden","diamond","netherite"};
        int[] durability={59,131,190,250,32,1561,2031};
        float[] speed={2,4,5,6,12,8,9},attack={0,1,1,2,0,3,4};
        for(int i=0;i<materials.length;i++) {
            check(materials[i].durability()==durability[i]*2,"tool tier doubles durability: "+names[i]);
            check(materials[i].speed()==speed[i]*1.5F+3F,"tool tier mining speed vanilla×1.5+3: "+names[i]);
            check(materials[i].attackDamageBonus()==attack[i]+4F,"tool tier attack damage vanilla+4: "+names[i]);
            for(String type:new String[]{"sword","pickaxe","axe","shovel","hoe"}) {
                Item item=Registries.ITEM.get(Identifier.ofVanilla(names[i]+"_"+type));
                check(item.getDefaultStack().getMaxDamage()==durability[i]*2,"tool item not double-counted: "+names[i]+"_"+type);
            }
        }
        check(attribute(Items.DIAMOND_SWORD,EntityAttributes.ATTACK_DAMAGE)==10,"diamond sword attack modifier 6→10 (+4)");
        check(attribute(Items.DIAMOND_PICKAXE,EntityAttributes.ATTACK_DAMAGE)==8,"diamond pickaxe attack modifier 4→8 (+4)");
        check(Items.DIAMOND_PICKAXE.getDefaultStack().getMiningSpeedMultiplier(Blocks.STONE.getDefaultState())==15,"diamond pickaxe stone speed 8→15");
        check(Items.SHEARS.getDefaultStack().getMiningSpeedMultiplier(Blocks.WHITE_WOOL.getDefaultState())==8,"shears wool speed 5→8");
        ArmorMaterial[] armor={ArmorMaterials.LEATHER,ArmorMaterials.COPPER,ArmorMaterials.CHAIN,ArmorMaterials.IRON,ArmorMaterials.GOLD,ArmorMaterials.DIAMOND,ArmorMaterials.TURTLE_SCUTE,ArmorMaterials.NETHERITE,ArmorMaterials.ARMADILLO_SCUTE};
        int[] chest={3,4,5,6,5,8,6,8,8},multiplier={5,11,15,15,7,33,25,37,4};
        for(int i=0;i<armor.length;i++) {
            check(armor[i].defense().get(EquipmentType.CHESTPLATE)==chest[i]+3,"armor material receives +3 defense: "+i);
            check(armor[i].durability()==multiplier[i]*2,"armor durability doubled once: "+i);
        }
        check(attribute(Items.DIAMOND_CHESTPLATE,EntityAttributes.ARMOR)==11,"diamond chestplate armor 8→11");
        check(attribute(Items.DIAMOND_CHESTPLATE,EntityAttributes.ARMOR_TOUGHNESS)==3,"diamond armor toughness 2→3");
        check(Items.DIAMOND_CHESTPLATE.getDefaultStack().getMaxDamage()==1056,"diamond chestplate durability 528→1056");
        check(attribute(Items.DIAMOND_HORSE_ARMOR,EntityAttributes.ARMOR)==14,"diamond horse armor 11→14");
        check(attribute(Items.WOLF_ARMOR,EntityAttributes.ARMOR)==14,"wolf body armor 11→14");
        check(Items.WOLF_ARMOR.getDefaultStack().getMaxDamage()==128,"wolf armor durability 64→128");
        check(attribute(Items.SHIELD,EntityAttributes.ARMOR)==3,"shield adds 3 offhand armor");
        check(attribute(Items.ELYTRA,EntityAttributes.ARMOR)==3,"elytra adds 3 chest armor");
        Item[] utility={Items.BOW,Items.CROSSBOW,Items.TRIDENT,Items.MACE,Items.SHIELD,Items.SHEARS,Items.BRUSH,Items.FISHING_ROD,Items.FLINT_AND_STEEL,Items.ELYTRA};
        int[] original={384,465,250,500,336,238,64,64,64,432};
        for(int i=0;i<utility.length;i++)check(utility[i].getDefaultStack().getMaxDamage()==original[i]*2,"utility durability doubled once: "+utility[i].getTranslationKey());
        check(attribute(Items.TRIDENT,EntityAttributes.ATTACK_DAMAGE)==12,"trident melee modifier 8→12");
        check(attribute(Items.MACE,EntityAttributes.ATTACK_DAMAGE)==9,"mace melee modifier 5→9");
        var shooter=player(server);
        var shieldMods=Items.SHIELD.getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        var wingMods=Items.ELYTRA.getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        check(shieldMods.modifiers().stream().noneMatch(a->wingMods.modifiers().stream().anyMatch(b->a.modifier().id().equals(b.modifier().id()))),
            "shield and elytra modifiers have independent identities and can stack");
        try {
            check(hit(server,shooter,Items.BOW,false)==6,"bow arrow actual hit 2→6 (+4 after velocity)");
            check(hit(server,shooter,Items.CROSSBOW,false)==6,"crossbow arrow actual hit 2→6 (+4 after velocity)");
            check(hit(server,shooter,Items.TRIDENT,true)==12,"thrown trident actual hit 8→12");
        } catch(ReflectiveOperationException error) {throw new IllegalStateException("Could not run projectile hit verification",error);}
        LoggerFactory.getLogger("diamondworld").info("DIAMONDWORLD ALL GEAR CHECKS PASSED");
    }
}
