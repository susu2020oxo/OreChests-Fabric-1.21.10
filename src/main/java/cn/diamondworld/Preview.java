package cn.diamondworld;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
/** Development-only scene, enabled solely by the previewWorld Gradle property. */
public final class Preview {
 public static void install() {
  if (!Boolean.getBoolean("diamondworld.preview")) return;
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{
   var p=handler.player;var w=server.getOverworld();
   w.getEntitiesByClass(net.minecraft.entity.mob.MobEntity.class,new net.minecraft.util.math.Box(-24,60,-24,24,100,24),e->true).forEach(net.minecraft.entity.Entity::discard);
   w.getEntitiesByClass(net.minecraft.entity.ItemEntity.class,new net.minecraft.util.math.Box(-24,60,-24,24,120,24),e->true).forEach(net.minecraft.entity.Entity::discard);
   for(int x=-22;x<=22;x++)for(int z=-22;z<=22;z++)w.setBlockState(new BlockPos(x,63,z),Blocks.GRASS_BLOCK.getDefaultState());
   for(int y=64;y<=69;y++)w.setBlockState(new BlockPos(0,y,0),Blocks.OAK_LOG.getDefaultState());
   for(int y=67;y<=70;y++)for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++)
    if((x!=0||z!=0||y==70)&&Math.abs(x)+Math.abs(z)<=(y==70?2:5))w.setBlockState(new BlockPos(x,y,z),Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT,true));
   // Deliberately different surfaces next to the grass and tree for visual regression inspection.
   for(int x=-12;x<=12;x++)for(int y=64;y<=66;y++)for(int z=6;z<=11;z++)w.setBlockState(new BlockPos(x,y,z),Blocks.AIR.getDefaultState());
   Block[] samples={Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.COPPER_ORE,Blocks.GOLD_ORE,Blocks.REDSTONE_ORE,Blocks.LAPIS_ORE,Blocks.EMERALD_ORE,Blocks.DIAMOND_ORE};
   Block[] deep={Blocks.DEEPSLATE_COAL_ORE,Blocks.DEEPSLATE_IRON_ORE,Blocks.DEEPSLATE_COPPER_ORE,Blocks.DEEPSLATE_GOLD_ORE,Blocks.DEEPSLATE_REDSTONE_ORE,Blocks.DEEPSLATE_LAPIS_ORE,Blocks.DEEPSLATE_EMERALD_ORE,Blocks.DEEPSLATE_DIAMOND_ORE};
   for(int i=0;i<8;i++){
    w.setBlockState(new BlockPos(-8+i*2,64,7),samples[i].getDefaultState());
    w.setBlockState(new BlockPos(-8+i*2,64,10),deep[i].getDefaultState());
   }
   for(int i=0;i<5;i++)w.setBlockState(new BlockPos(-5+i,64,5),new Block[]{Blocks.OAK_PLANKS,Blocks.COBBLESTONE,Blocks.CRAFTING_TABLE,Blocks.FURNACE,Blocks.DIAMOND_BLOCK}[i].getDefaultState());
   var cow=EntityType.COW.create(w,SpawnReason.COMMAND);cow.refreshPositionAndAngles(-4,64,2,20,0);cow.setAiDisabled(true);w.spawnEntity(cow);
   var sheep=EntityType.SHEEP.create(w,SpawnReason.COMMAND);sheep.refreshPositionAndAngles(4,64,2,-30,0);sheep.setAiDisabled(true);w.spawnEntity(sheep);
   w.setTimeOfDay(6000);w.setWeather(0,100000,false,false);
   p.changeGameMode(GameMode.CREATIVE);p.getAbilities().flying=true;p.sendAbilitiesUpdate();handler.requestTeleport(14,70,19,140,20);
   p.getInventory().clear();
   p.giveItemStack(new ItemStack(Items.DIAMOND_PICKAXE));
   for(var type:new EntityType<?>[]{EntityType.BAT,EntityType.CHICKEN,EntityType.SPIDER,EntityType.ENDERMAN,EntityType.CREEPER,EntityType.GUARDIAN,EntityType.WARDEN})p.giveItemStack(new ItemStack(DiamondWorld.ESSENCES.get(type),3));
  });
 }
}
