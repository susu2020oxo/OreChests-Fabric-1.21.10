package cn.diamondworld;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameMode;
import org.slf4j.LoggerFactory;

/** Actual consumption, save/load and death-replacement checks. Enabled only by the development verification run. */
public final class PermanentHealthVerification {
    private static void check(boolean value, String message) {
        if (!value) throw new IllegalStateException("PERMANENT HEALTH CHECK FAILED: " + message);
        LoggerFactory.getLogger("diamondworld").info("CHECK PASS: {}", message);
    }
    private static ServerPlayerEntity player(MinecraftServer server) {
        var world=server.getOverworld();
        var profile=new GameProfile(UUID.randomUUID(),"HealthCheck");
        var p=new ServerPlayerEntity(server,world,profile,SyncedClientOptions.createDefault());
        p.networkHandler=new ServerPlayNetworkHandler(server,new ClientConnection(NetworkSide.SERVERBOUND),p,
            ConnectedClientData.createDefault(profile,false));
        p.refreshPositionAndAngles(2,195,2,0,0);p.changeGameMode(GameMode.SURVIVAL);
        return p;
    }
    private static void consume(ServerPlayerEntity p,ItemStack stack) {
        var consumable=stack.get(DataComponentTypes.CONSUMABLE);
        check(consumable!=null,"test item has a real consumption component: "+stack.getItem().getTranslationKey());
        p.getHungerManager().setFoodLevel(10);
        consumable.finishConsumption(p.getEntityWorld(),p,stack);
    }
    public static void run(MinecraftServer server) {
        var p=player(server);var loaded=player(server);var respawned=player(server);var capped=player(server);var aliveCopy=player(server);
        try {
            float initial=p.getMaxHealth();
            var one=new ItemStack(Items.BREAD);
            consume(p,one);
            check(one.isEmpty() && p.getMaxHealth()==initial+1,
                "last item of a food stack grants exactly one permanent health point before decrement");
            consume(p,new ItemStack(Items.BREAD));
            check(p.getMaxHealth()==initial+2,"eating the same food again permanently stacks another health point");
            var diamondFood=net.minecraft.registry.Registries.ITEM.get(net.minecraft.util.Identifier.of("diamondworld","diamond_beef"));
            consume(p,new ItemStack(diamondFood));
            check(p.getMaxHealth()==initial+3 && p.hasStatusEffect(StatusEffects.REGENERATION),
                "diamond food adds permanent health while retaining its original regeneration effect");
            consume(p,new ItemStack(DiamondWorld.ESSENCES.get(EntityType.COW)));
            check(p.getMaxHealth()==initial+4,"edible mob essence also grants permanent health");
            consume(p,new ItemStack(Items.MILK_BUCKET));
            check(p.getMaxHealth()==initial+4 && !p.hasStatusEffect(StatusEffects.REGENERATION),
                "actual milk removes temporary effects but neither removes nor grants permanent health");
            consume(p,new ItemStack(Items.POTION));
            check(p.getMaxHealth()==initial+4,"drinking a non-food potion does not grant permanent health");

            var saved=NbtWriteView.create(ErrorReporter.EMPTY,server.getRegistryManager());
            p.writeData(saved);
            loaded.readData(NbtReadView.create(ErrorReporter.EMPTY,server.getRegistryManager(),saved.getNbt()));
            check(loaded.getMaxHealth()==initial+4 && PermanentHealth.bonus(loaded)==4,
                "real player NBT save and load preserves food health without a status effect");
            loaded.clearStatusEffects();
            check(loaded.getMaxHealth()==initial+4,"clearing all effects after reconnect preserves permanent food health");
            respawned.copyFrom(loaded,false);
            check(respawned.getMaxHealth()==initial+4 && PermanentHealth.bonus(respawned)==4,
                "actual death-respawn copy preserves accumulated permanent food health");
            aliveCopy.copyFrom(loaded,true);
            check(aliveCopy.getMaxHealth()==initial+4,"alive player replacement does not duplicate the permanent modifier");

            var world=server.getOverworld();var cakePos=new BlockPos(2,196,2);
            world.setBlockState(cakePos.down(),Blocks.STONE.getDefaultState());
            world.setBlockState(cakePos,Blocks.CAKE.getDefaultState());
            p.getHungerManager().setFoodLevel(10);
            var state=world.getBlockState(cakePos);
            var result=state.onUse(world,p,new BlockHitResult(Vec3d.ofCenter(cakePos),Direction.UP,cakePos,false));
            check(result.isAccepted() && world.getBlockState(cakePos).get(CakeBlock.BITES)==1 && p.getMaxHealth()==initial+5,
                "eating a placed cake slice grants one permanent health point");
            p.getHungerManager().setFoodLevel(20);
            world.getBlockState(cakePos).onUse(world,p,new BlockHitResult(Vec3d.ofCenter(cakePos),Direction.UP,cakePos,false));
            check(p.getMaxHealth()==initial+5,"a refused cake interaction grants no health");
            world.setBlockState(cakePos,Blocks.AIR.getDefaultState());

            capped.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(1023);
            consume(capped,new ItemStack(Items.BREAD));
            consume(capped,new ItemStack(Items.BREAD));
            check(capped.getMaxHealth()==1024 && PermanentHealth.bonus(capped)==1,
                "permanent food health respects the vanilla 1024-point maximum");
            LoggerFactory.getLogger("diamondworld").info("DIAMONDWORLD PERMANENT HEALTH CHECKS PASSED");
        } finally {
            p.discard();loaded.discard();respawned.discard();capped.discard();aliveCopy.discard();
        }
    }
}
