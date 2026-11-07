package cn.diamondworld;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;

/** Server-only enemy traits. Hooks run on the relevant mob/projectile, never scan the world. */
public final class HostileTraits {
    private static final Map<MobEntity, TrailState> STATES = new WeakHashMap<>();
    private record TrailState(Vec3d pos, boolean grounded) {}
    private HostileTraits() {}
    public static void install() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((target,source,base,damage,blocked)->{
            if(!blocked && damage>0 && source.getAttacker() instanceof MobEntity attacker && source.getSource()==attacker && EssenceAbilities.isMelee(source))
                onMeleeHit(attacker,target);
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((target,source)->{
            if(source.getAttacker() instanceof MobEntity attacker && source.getSource()==attacker && EssenceAbilities.isMelee(source)) onMeleeHit(attacker,target);
        });
    }
    public static boolean enabled(ServerWorld world) {
        return world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }
    /** Only terrain may be replaced. Containers, constructed blocks, portals and bedrock are excluded. */
    public static boolean isNaturalTerrain(BlockState state) {
        return state.isIn(BlockTags.BASE_STONE_OVERWORLD) || state.isIn(BlockTags.BASE_STONE_NETHER)
            || state.isIn(BlockTags.DIRT) || state.isIn(BlockTags.SAND)
            || state.isOf(Blocks.GRAVEL) || state.isOf(Blocks.CLAY) || state.isOf(Blocks.END_STONE)
            || state.isOf(Blocks.SCULK) || state.isOf(Blocks.MOSS_BLOCK) || state.isOf(Blocks.SNOW_BLOCK)
            || state.isOf(Blocks.SOUL_SAND) || state.isOf(Blocks.SOUL_SOIL)
            || state.isOf(Blocks.INFESTED_STONE) || state.isOf(Blocks.INFESTED_DEEPSLATE);
    }
    /** Returns true only when a new ore block was placed; safe for integration tests too. */
    public static boolean crystallize(ServerWorld world, BlockPos pos, boolean allowAir) {
        if (!enabled(world) || !world.isInBuildLimit(pos) || !world.isChunkLoaded(pos) || world.getBlockEntity(pos) != null) return false;
        BlockState state = world.getBlockState(pos);
        if (!isNaturalTerrain(state) && !(allowAir && (state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.FIRE)))) return false;
        BlockState ore = (state.isIn(BlockTags.DEEPSLATE_ORE_REPLACEABLES) || state.isOf(Blocks.INFESTED_DEEPSLATE)
            ? Blocks.DEEPSLATE_DIAMOND_ORE : Blocks.DIAMOND_ORE).getDefaultState();
        return world.setBlockState(pos, ore, Block.NOTIFY_ALL);
    }
    public static int surfacePatch(ServerWorld world, BlockPos around, int radius, int budget) {
        int placed=0;
        for(int dx=-radius;dx<=radius;dx++) for(int dz=-radius;dz<=radius;dz++) {
            if(dx*dx+dz*dz>radius*radius) continue;
            for(int dy=1;dy>=-4;dy--) {
                BlockPos p=around.add(dx,dy,dz); if(!world.isChunkLoaded(p))continue;BlockState state=world.getBlockState(p);
                if(isNaturalTerrain(state) && (world.getBlockState(p.up()).isAir() || !world.getFluidState(p.up()).isEmpty())) {
                    if(crystallize(world,p,false) && ++placed>=budget) return placed;
                    break;
                }
            }
        }
        return placed;
    }
    /** Actual explosion callback, after terrain destruction, so the exposed crater remains open. */
    public static int afterExplosion(Explosion explosion) {
        Entity cause=explosion.getCausingEntity();
        if(!(cause instanceof CreeperEntity || cause instanceof GhastEntity || cause instanceof WitherEntity || cause instanceof BreezeEntity)) return 0;
        ServerWorld world=explosion.getWorld(); if(!enabled(world)) return 0;
        BlockPos center=BlockPos.ofFloored(explosion.getPosition());
        int radius=Math.min(8,Math.max(3,(int)Math.ceil(explosion.getPower()*1.4)+1)),placed=0;
        for(int y=-radius;y<=radius;y++)for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++) {
            if(x*x+y*y+z*z>radius*radius)continue;
            BlockPos p=center.add(x,y,z);
            if(!world.isChunkLoaded(p))continue;
            if(!isNaturalTerrain(world.getBlockState(p)))continue;
            boolean exposed=false;
            for(Direction dir:Direction.values()) {
                if(!world.isChunkLoaded(p.offset(dir)))continue;
                BlockState neighbor=world.getBlockState(p.offset(dir));
                if(neighbor.isAir() || !neighbor.getFluidState().isEmpty()) {exposed=true;break;}
            }
            if(exposed && crystallize(world,p,false) && ++placed>=512)return placed;
        }
        // Breeze/airburst explosions can occur above unbroken ground.
        if(placed==0) placed+=surfacePatch(world,center,Math.min(3,radius),24);
        return placed;
    }
    public static boolean isSkeletonArrow(PersistentProjectileEntity arrow) {
        return arrow.getOwner() instanceof AbstractSkeletonEntity || arrow.getOwner() instanceof IllusionerEntity;
    }
    /** A solid, one-block-wide ore trail behind a flying skeleton arrow; capped at 40 blocks per arrow. */
    public static int arrowTrail(PersistentProjectileEntity arrow, Vec3d from, Vec3d to, int remaining) {
        if(!(arrow.getEntityWorld() instanceof ServerWorld world) || !enabled(world) || !isSkeletonArrow(arrow) || remaining<=0 || arrow.age<3) return 0;
        double distance=from.distanceTo(to);if(distance<0.05 || distance>12)return 0;
        int steps=Math.min(32,(int)Math.ceil(distance*4)),placed=0;
        BlockPos current=BlockPos.ofFloored(to);Entity owner=arrow.getOwner();
        for(int i=0;i<steps;i++) {
            Vec3d v=from.lerp(to,i/(double)steps);BlockPos p=BlockPos.ofFloored(v);
            if(p.equals(current) || v.squaredDistanceTo(to)<0.8 || owner.squaredDistanceTo(v)<9)continue;
            if(crystallize(world,p,true) && ++placed>=remaining)break;
        }
        return placed;
    }
    public static void arrowImpact(PersistentProjectileEntity arrow, BlockPos hit) {
        if(!(arrow.getEntityWorld() instanceof ServerWorld world) || !(isSkeletonArrow(arrow) || arrow.getOwner() instanceof PillagerEntity || arrow.getOwner() instanceof DrownedEntity) || !enabled(world))return;
        crystallize(world,hit,false);
        for(Direction dir:Direction.Type.HORIZONTAL)crystallize(world,hit.offset(dir),false);
    }
    public static void dragonBreath(ProjectileEntity fireball,Vec3d position) {
        if(fireball.getEntityWorld() instanceof ServerWorld world && fireball.getOwner() instanceof EnderDragonEntity && enabled(world))
            surfacePatch(world,BlockPos.ofFloored(position),3,29);
    }
    public static void shulkerImpact(ProjectileEntity bullet,Vec3d position) {
        if(bullet.getEntityWorld() instanceof ServerWorld world && bullet.getOwner() instanceof ShulkerEntity && enabled(world)) {
            BlockPos p=BlockPos.ofFloored(position);surfacePatch(world,p,1,5);
            for(Direction d:Direction.values())crystallize(world,p.offset(d),false);
        }
    }
    public static void onMeleeHit(MobEntity attacker,LivingEntity target) {
        if(!(attacker.getEntityWorld() instanceof ServerWorld world) || !enabled(world))return;
        BlockPos pos=target.getBlockPos();
        if(attacker instanceof VindicatorEntity || attacker instanceof PiglinBruteEntity || attacker instanceof WitherSkeletonEntity)
            surfacePatch(world,pos,1,5);
        if(attacker instanceof HoglinEntity || attacker instanceof ZoglinEntity) {
            Vec3d from=attacker.getEntityPos(),to=target.getEntityPos();
            for(int i=0;i<4;i++)surfacePatch(world,BlockPos.ofFloored(from.lerp(to,i/3.0)),1,4);
        }
        if(attacker instanceof VexEntity) {
            for(Direction dir:Direction.values())crystallize(world,pos.offset(dir),false);
        }
        if(attacker instanceof PiglinEntity || attacker instanceof ZombifiedPiglinEntity)surfacePatch(world,pos,0,1);
    }
    public static void witchSplash(ProjectileEntity potion, Vec3d position) {
        if(potion.getEntityWorld() instanceof ServerWorld world && potion.getOwner() instanceof WitchEntity && enabled(world))
            surfacePatch(world,BlockPos.ofFloored(position),2,13);
    }
    public static void evokerFang(EvokerFangsEntity fangs) {
        if(fangs.getEntityWorld() instanceof ServerWorld world && fangs.getOwner() instanceof EvokerEntity && enabled(world))
            surfacePatch(world,fangs.getBlockPos(),0,1);
    }
    /** Blaze fire turns the struck surface into crystal instead of only leaving fire. */
    public static void fireballImpact(ProjectileEntity projectile, BlockPos hit) {
        if(!(projectile.getEntityWorld() instanceof ServerWorld world) || !(projectile.getOwner() instanceof BlazeEntity) || !enabled(world))return;
        crystallize(world,hit,false);surfacePatch(world,hit.up(),1,5);
        if(world.getBlockState(hit.up()).isOf(Blocks.FIRE))world.setBlockState(hit.up(),Blocks.AIR.getDefaultState(),Block.NOTIFY_ALL);
    }
    /** Warden's real sonic shot creates a ground fissure toward the target, up to 24 ore blocks. */
    public static void sonicFissure(ServerWorld world,WardenEntity warden,LivingEntity target) {
        if(!enabled(world))return;
        Vec3d start=warden.getEntityPos(),end=target.getEntityPos();
        int steps=Math.min(24,Math.max(1,(int)Math.ceil(start.distanceTo(end))));
        for(int i=0;i<=steps;i++)surfacePatch(world,BlockPos.ofFloored(start.lerp(end,i/(double)steps)),0,1);
    }
    public static void onMobTick(MobEntity mob) {
        if(!(mob.getEntityWorld() instanceof ServerWorld world) || !mob.isAlive() || !enabled(world))return;
        // Never apply terrain traits to peaceful creatures, including unprovoked neutral animals.
        if(!(mob instanceof HostileEntity || mob instanceof SlimeEntity || mob instanceof GhastEntity || mob instanceof PhantomEntity))return;
        TrailState previous=STATES.put(mob,new TrailState(mob.getEntityPos(),mob.isOnGround()));
        boolean chasing=mob.getTarget()!=null;
        BlockPos pos=mob.getBlockPos();
        if(mob instanceof EndermanEntity) {
            if(chasing && previous!=null && previous.pos.squaredDistanceTo(mob.getEntityPos())>25) {
                surfacePatch(world,BlockPos.ofFloored(previous.pos),1,5);surfacePatch(world,pos,1,5);
            }
            return;
        }
        if(mob instanceof SlimeEntity slime) {
            if(previous!=null && !previous.grounded && mob.isOnGround())surfacePatch(world,pos,Math.min(2,Math.max(1,slime.getSize()/2)),9);
            return;
        }
        if(mob instanceof SpiderEntity && chasing && mob.age%40==0 && previous!=null) {
            surfacePatch(world,pos,1,4);
            // Leave a crystalline web behind rather than encasing the spider or its target.
            Vec3d backwards=mob.getVelocity().multiply(-3);
            BlockPos web=BlockPos.ofFloored(mob.getEntityPos().add(backwards));
            if(!web.equals(pos) && world.isAir(web) && world.getBlockState(web.down()).isSolidBlock(world,web.down()))
                world.setBlockState(web,Blocks.COBWEB.getDefaultState(),Block.NOTIFY_ALL);
            return;
        }
        if(mob instanceof GuardianEntity guardian && guardian.hasBeamTarget() && mob.age%20==0) {
            LivingEntity target=guardian.getBeamTarget();
            if(target!=null) {
                Vec3d a=mob.getEntityPos(),b=target.getEntityPos();
                for(int i=0;i<6;i++)surfacePatch(world,BlockPos.ofFloored(a.lerp(b,i/5.0)),0,1);
            }
            return;
        }
        if((mob instanceof SilverfishEntity || mob instanceof EndermiteEntity) && mob.age%60==0) {
            surfacePatch(world,pos,1,3);return;
        }
        if(mob instanceof ZombieEntity && chasing && mob.age%40==0) {
            surfacePatch(world,pos,0,1);return;
        }
        if(mob instanceof RavagerEntity && chasing && mob.age%20==0)surfacePatch(world,pos,1,5);
        if(mob instanceof PhantomEntity && chasing && mob.age%40==0)surfacePatch(world,pos.down(),1,4);
        if(mob instanceof CreakingEntity && chasing && mob.age%60==0)surfacePatch(world,pos,1,3);
    }
}
