package cn.diamondworld;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.LoggerFactory;

/** Opt-in, real server-world integration checks called by Verification. Never runs during normal play. */
public final class V11Verification {
    private static void check(boolean result, String message) {
        if (!result) throw new IllegalStateException("V1.1 CHECK FAILED: " + message);
        LoggerFactory.getLogger("diamondworld").info("CHECK PASS: {}", message);
    }
    private static ServerPlayerEntity player(MinecraftServer server, ServerWorld world) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "DiamondCheck");
        var p = new ServerPlayerEntity(server, world, profile, SyncedClientOptions.createDefault());
        // An unconnected handler queues outgoing packets; no socket or real client is needed.
        p.networkHandler = new ServerPlayNetworkHandler(server,
            new ClientConnection(NetworkSide.SERVERBOUND), p, ConnectedClientData.createDefault(profile, false));
        p.refreshPositionAndAngles(2, 185, 2, 0, 0);
        p.changeGameMode(GameMode.SURVIVAL);
        return p;
    }
    private static void consume(ServerPlayerEntity p, EntityType<?> type) {
        p.clearStatusEffects();
        ItemStack stack = new ItemStack(DiamondWorld.ESSENCES.get(type));
        var component = stack.get(DataComponentTypes.CONSUMABLE);
        check(component != null, "essence has consumable component: " + type.getTranslationKey());
        component.finishConsumption(p.getEntityWorld(), p, stack);
    }
    private static int ores(ServerWorld w, int x1, int y1, int z1, int x2, int y2, int z2) {
        int count = 0;
        for (int x=x1;x<=x2;x++) for (int y=y1;y<=y2;y++) for (int z=z1;z<=z2;z++) {
            var s=w.getBlockState(new BlockPos(x,y,z));
            if(s.isOf(Blocks.DIAMOND_ORE) || s.isOf(Blocks.DEEPSLATE_DIAMOND_ORE)) count++;
        }
        return count;
    }
    private static void stoneFloor(ServerWorld w, int top) {
        for(int x=0;x<16;x++) for(int z=0;z<16;z++) for(int y=top-5;y<=top;y++)
            w.setBlockState(new BlockPos(x,y,z),Blocks.STONE.getDefaultState(),Block.NOTIFY_ALL);
    }
    public static void run(MinecraftServer server) {
        var world=server.getOverworld();
        world.getChunk(0,0);
        boolean originalGrief=world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        var p=player(server,world);
        try {
            for (var entry:DiamondWorld.ESSENCES.entrySet()) {
                var consumable=entry.getValue().getDefaultStack().get(DataComponentTypes.CONSUMABLE);
                check(consumable != null && !consumable.onConsumeEffects().isEmpty(),
                    "species essence has real consumption effects: "+entry.getKey().getTranslationKey());
            }
            consume(p,EntityType.BAT);
            check(p.hasStatusEffect(EssenceAbilities.FLIGHT) && p.hasStatusEffect(StatusEffects.NIGHT_VISION),
                "bat essence grants timed flight and night vision");
            EssenceAbilities.updateFlight(p);
            check(p.getAbilities().allowFlying,"bat essence enables survival flight permission");
            p.getAbilities().flying=true;
            var saved=net.minecraft.storage.NbtWriteView.create(net.minecraft.util.ErrorReporter.EMPTY,world.getRegistryManager());
            p.writeData(saved);
            var savedAbilities=saved.getNbt().getCompoundOrEmpty("abilities");
            check(!savedAbilities.getBoolean("mayfly",true) && !savedAbilities.getBoolean("flying",true),
                "autosave serializes original survival flight permissions while bat ability is active");
            check(p.getAbilities().allowFlying && p.getAbilities().flying,
                "autosave restores live temporary flight immediately after serialization");
            p.removeStatusEffect(EssenceAbilities.FLIGHT);
            EssenceAbilities.updateFlight(p);
            check(!p.getAbilities().allowFlying && !p.getAbilities().flying && p.hasStatusEffect(StatusEffects.SLOW_FALLING),
                "flight expiration revokes flight and grants a safe slow-fall landing");
            consume(p,EntityType.BAT);
            EssenceAbilities.updateFlight(p);
            var milk=new ItemStack(Items.MILK_BUCKET);
            milk.get(DataComponentTypes.CONSUMABLE).finishConsumption(world,p,milk);
            EssenceAbilities.updateFlight(p);
            check(!p.getAbilities().allowFlying && !p.hasStatusEffect(EssenceAbilities.FLIGHT),
                "drinking actual milk removes essence flight and survival permission");
            p.changeGameMode(GameMode.CREATIVE);
            consume(p,EntityType.BAT);EssenceAbilities.updateFlight(p);
            p.clearStatusEffects();EssenceAbilities.updateFlight(p);
            check(p.getAbilities().allowFlying,"creative flight is preserved after essence expiration");
            p.changeGameMode(GameMode.SURVIVAL);
            consume(p,EntityType.CHICKEN);
            check(p.hasStatusEffect(StatusEffects.SLOW_FALLING),"chicken essence grants slow falling");
            consume(p,EntityType.SPIDER);p.horizontalCollision=true;
            check(p.hasStatusEffect(EssenceAbilities.CLIMBING) && p.isClimbing(),
                "spider essence makes a survival player climb a contacted wall");
            p.horizontalCollision=false;
            consume(p,EntityType.ENDERMAN);
            check(p.hasStatusEffect(EssenceAbilities.BLINK),"enderman essence enables blink ability");
            consume(p,EntityType.GUARDIAN);
            check(p.hasStatusEffect(EssenceAbilities.THORNS) && p.hasStatusEffect(StatusEffects.WATER_BREATHING),
                "guardian essence grants thorns and water breathing");

            world.getGameRules().get(GameRules.DO_MOB_GRIEFING).set(true,server);
            stoneFloor(world,139);
            var creeper=EntityType.CREEPER.create(world,net.minecraft.entity.SpawnReason.COMMAND);
            creeper.refreshPositionAndAngles(8,140,8,0,0);
            world.createExplosion(creeper,8,140,8,3F,World.ExplosionSourceType.MOB);
            int craterOres=ores(world,0,134,0,15,140,15);
            check(craterOres>0,"actual creeper explosion crystallizes its exposed crater ("+craterOres+" ores)");
            check(world.isAir(new BlockPos(8,139,8)),"crystallization leaves an open crater rather than filling the blast with ore");

            BlockPos chest=new BlockPos(14,160,14),bedrock=chest.west();
            world.setBlockState(chest,Blocks.CHEST.getDefaultState());
            world.setBlockState(bedrock,Blocks.BEDROCK.getDefaultState());
            check(!HostileTraits.crystallize(world,chest,true) && !HostileTraits.crystallize(world,bedrock,true)
                && world.getBlockState(chest).isOf(Blocks.CHEST) && world.getBlockState(bedrock).isOf(Blocks.BEDROCK),
                "crystallization preserves containers and bedrock");
            world.getGameRules().get(GameRules.DO_MOB_GRIEFING).set(false,server);
            BlockPos protectedStone=new BlockPos(10,160,10);
            world.setBlockState(protectedStone,Blocks.STONE.getDefaultState());
            check(!HostileTraits.crystallize(world,protectedStone,false) && world.getBlockState(protectedStone).isOf(Blocks.STONE),
                "mobGriefing=false disables direct crystallization");
            stoneFloor(world,154);
            world.createExplosion(creeper,8,155,8,3F,World.ExplosionSourceType.MOB);
            check(ores(world,0,149,0,15,155,15)==0 && world.getBlockState(new BlockPos(8,154,8)).isOf(Blocks.STONE),
                "mobGriefing=false disables explosion crater conversion and terrain destruction");
            world.getGameRules().get(GameRules.DO_MOB_GRIEFING).set(true,server);

            for(int x=0;x<16;x++)for(int y=168;y<=174;y++)for(int z=8;z<16;z++)
                world.setBlockState(new BlockPos(x,y,z),Blocks.AIR.getDefaultState());
            var skeleton=EntityType.SKELETON.create(world,net.minecraft.entity.SpawnReason.COMMAND);
            skeleton.refreshPositionAndAngles(1,170,10,0,0);
            var arrow=new ArrowEntity(EntityType.ARROW,world);
            arrow.setOwner(skeleton);arrow.refreshPositionAndAngles(2,170,10,0,0);arrow.age=3;arrow.setVelocity(2.5,0,0);
            for(int i=0;i<4;i++)arrow.tick();
            check(ores(world,0,168,9,15,173,11)>0,"actual skeleton arrow ticks leave airborne diamond-ore trail");
            arrow.discard();
            var playerArrow=new ArrowEntity(EntityType.ARROW,world);
            p.refreshPositionAndAngles(1,170,14,0,0);
            playerArrow.setOwner(p);playerArrow.refreshPositionAndAngles(2,170,14,0,0);playerArrow.age=3;playerArrow.setVelocity(2.5,0,0);
            for(int i=0;i<4;i++)playerArrow.tick();
            check(ores(world,0,168,13,15,173,15)==0,"player arrows do not create hostile ore trails");
            playerArrow.discard();
            LoggerFactory.getLogger("diamondworld").info("DIAMONDWORLD V1.1 INTEGRATION CHECKS PASSED");
        } finally {
            world.getGameRules().get(GameRules.DO_MOB_GRIEFING).set(originalGrief,server);
            p.clearStatusEffects();EssenceAbilities.restoreFlight(p,false);p.discard();
        }
    }
}
