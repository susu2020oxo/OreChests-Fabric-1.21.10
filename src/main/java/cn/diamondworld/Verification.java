package cn.diamondworld;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.ItemEntity;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.slf4j.LoggerFactory;

/** Opt-in integration verification, only enabled with -Ddiamondworld.verify=true. */
public final class Verification {
 private static void check(boolean result,String message) {
  if (!result) throw new IllegalStateException("DIAMONDWORLD CHECK FAILED: "+message);
  LoggerFactory.getLogger("diamondworld").info("CHECK PASS: {}",message);
 }
 public static void install() {
  if (!Boolean.getBoolean("diamondworld.verify")) return;
  ServerLifecycleEvents.SERVER_STARTED.register(server -> server.getOverworld().setChunkForced(0,0,true));
  int[] ticks={0};
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
   if (++ticks[0] != 40) return;
   try {
    var world=server.getOverworld();var pos=new BlockPos(0,100,0);world.getChunk(0,0);
    check(Items.WOODEN_PICKAXE.getDefaultStack().getMaxDamage()==118,"wooden pickaxe durability doubled");
    check(ToolMaterial.DIAMOND.speed()==12F,"diamond mining speed boosted");
    check(ToolMaterial.STONE.attackDamageBonus()==3F,"stone tool attack damage boosted");
    var grass=Block.getDroppedStacks(Blocks.GRASS_BLOCK.getDefaultState(),world,pos,null);
    check(grass.size()==1 && grass.getFirst().isOf(DiamondWorld.fragment),"grass drops diamond fragment");
    var stone=Block.getDroppedStacks(Blocks.STONE.getDefaultState(),world,pos,null,null,new ItemStack(Items.IRON_PICKAXE));
    check(stone.size()==1 && stone.getFirst().isOf(Items.COBBLESTONE),"stone still drops cobblestone");
    var log=Block.getDroppedStacks(Blocks.OAK_LOG.getDefaultState(),world,pos,null);
    check(log.size()==1 && log.getFirst().isOf(Items.OAK_LOG),"wood progression preserved");
    var input=CraftingRecipeInput.create(3,1,List.of(new ItemStack(DiamondWorld.fragment),new ItemStack(DiamondWorld.fragment),new ItemStack(DiamondWorld.fragment)));
    var recipe=server.getRecipeManager().getFirstMatch(RecipeType.CRAFTING,input,world);
    check(recipe.isPresent() && recipe.get().value().craft(input,world.getRegistryManager()).isOf(Items.DIAMOND),"three fragments craft one diamond");
    check(server.getRecipeManager().get(RegistryKey.of(RegistryKeys.RECIPE,Identifier.of("diamondworld","beef_smelting"))).isPresent(),"diamond beef smelting recipe loaded");
    world.getEntitiesByClass(ItemEntity.class,new Box(-20,80,-20,20,120,20),e->true).forEach(net.minecraft.entity.Entity::discard);
    var cow=EntityType.COW.create(world,SpawnReason.COMMAND);cow.refreshPositionAndAngles(0,100,0,0,0);world.spawnEntity(cow);
    var beef=Registries.ITEM.get(Identifier.of("diamondworld","diamond_beef"));
    var food=new ItemStack(beef);food.get(DataComponentTypes.CONSUMABLE).finishConsumption(world,cow,food);
    check(cow.hasStatusEffect(StatusEffects.REGENERATION),"diamond beef applies regeneration on consumption");
    boolean dead=cow.damage(world,world.getDamageSources().genericKill(),10000);
    LoggerFactory.getLogger("diamondworld").info("Cow damage={} health={} alive={}",dead,cow.getHealth(),cow.isAlive());
    var drops=world.getEntitiesByClass(ItemEntity.class,new Box(-5,95,-5,5,105,5),e->true);
    LoggerFactory.getLogger("diamondworld").info("Observed drops: {}",drops.stream().map(e->e.getStack().toString()).toList());
    check(drops.stream().anyMatch(e->e.getStack().isOf(beef)),"cow actually drops diamond beef");
    check(drops.stream().anyMatch(e->e.getStack().isOf(DiamondWorld.ESSENCES.get(EntityType.COW))),"cow also drops species essence");
    var sheep=EntityType.SHEEP.create(world,SpawnReason.COMMAND);sheep.refreshPositionAndAngles(10,100,0,0,0);world.spawnEntity(sheep);sheep.damage(world,world.getDamageSources().genericKill(),10000);
    var mutton=Registries.ITEM.get(Identifier.of("diamondworld","diamond_mutton"));
    check(world.getEntitiesByClass(ItemEntity.class,new Box(5,95,-5,15,105,5),e->e.getStack().isOf(mutton)).size()>0,"sheep actually drops diamond mutton");
    V11Verification.run(server);
    java.util.Map<String,Object> profiles=new java.util.TreeMap<>();
    for(var entry:DiamondWorld.ESSENCES.entrySet()) {
        String species=Registries.ENTITY_TYPE.getId(entry.getKey()).getPath();
        java.util.List<java.util.Map<String,Object>> effects=new java.util.ArrayList<>();
        for(var effect:EssenceAbilities.effects(species))effects.add(java.util.Map.of("effect",effect.getTranslationKey(),"seconds",effect.getDuration()/20,"level",effect.getAmplifier()+1));
        profiles.put(species,effects);
    }
    java.nio.file.Files.writeString(java.nio.file.Path.of("v11-essence-effects.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(profiles));
    LoggerFactory.getLogger("diamondworld").info("DIAMONDWORLD ALL INTEGRATION CHECKS PASSED");
   } catch(Throwable error) {LoggerFactory.getLogger("diamondworld").error("DIAMONDWORLD VERIFICATION FAILED",error);}
   finally {server.stop(false);}
  });
 }
}
