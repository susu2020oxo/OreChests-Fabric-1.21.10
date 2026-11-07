package cn.diamondworld;

import java.util.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Species abilities are actual status effects, synchronized to clients and removable with milk. */
public final class EssenceAbilities {
    private static final Map<UUID, FlightState> FLIGHTS = new HashMap<>();
    private static final Map<UUID, Boolean> SNEAKING = new HashMap<>();
    private static final Map<UUID, Long> LAST_ACTION = new HashMap<>();
    private static boolean reflecting;
    private record FlightState(boolean allowed, float speed) { }
    private static final class PowerEffect extends StatusEffect {
        PowerEffect() { super(StatusEffectCategory.BENEFICIAL, 0x46E5E0); }
    }
    private static RegistryEntry<StatusEffect> power(String id) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(DiamondWorld.ID,id), new PowerEffect());
    }
    public static final RegistryEntry<StatusEffect> FLIGHT=power("flight"), CLIMBING=power("climbing"),
        BLINK=power("blink"), THORNS=power("thorns"), FIRE_TOUCH=power("fire_touch"),
        VENOM_TOUCH=power("venom_touch"), WITHER_TOUCH=power("wither_touch"),
        CREEPER_POWER=power("creeper_power"), ECHO=power("echo"),
        PROSPECTING=power("prospecting"), ROOTED_GUARD=power("rooted_guard"),
        SPIT=power("spit"), SNOW_THROW=power("snow_throw"), RAM=power("ram"), FELINE_WARD=power("feline_ward");

    private static void add(List<StatusEffectInstance> out, RegistryEntry<StatusEffect> effect, int seconds, int level) {
        out.add(new StatusEffectInstance(effect,seconds*20,level));
    }
    public static List<StatusEffectInstance> effects(String species) {
        List<StatusEffectInstance> e=new ArrayList<>();
        add(e,StatusEffects.REGENERATION,8,0);
        switch(species) {
            case "bat" -> { add(e,FLIGHT,15,0);add(e,StatusEffects.NIGHT_VISION,30,0); }
            case "allay", "parrot" -> { add(e,FLIGHT,15,0);add(e,StatusEffects.SLOW_FALLING,25,0); }
            case "bee" -> { add(e,FLIGHT,12,0);add(e,VENOM_TOUCH,25,0); }
            case "chicken" -> add(e,StatusEffects.SLOW_FALLING,40,0);
            case "phantom" -> { add(e,FLIGHT,20,0);add(e,StatusEffects.NIGHT_VISION,30,0); }
            case "vex" -> { add(e,FLIGHT,15,0);add(e,StatusEffects.INVISIBILITY,25,0); }
            case "ghast", "happy_ghast" -> { add(e,FLIGHT,20,0);add(e,StatusEffects.FIRE_RESISTANCE,40,0); }
            case "blaze" -> { add(e,FLIGHT,12,0);add(e,StatusEffects.FIRE_RESISTANCE,40,0);add(e,FIRE_TOUCH,30,0); }
            case "spider" -> { add(e,CLIMBING,35,0);add(e,StatusEffects.NIGHT_VISION,35,0); }
            case "cave_spider" -> { add(e,CLIMBING,35,0);add(e,VENOM_TOUCH,30,0); }
            case "enderman", "endermite" -> { add(e,BLINK,30,0);add(e,StatusEffects.NIGHT_VISION,30,0); }
            case "creeper" -> { add(e,CREEPER_POWER,30,0);add(e,StatusEffects.RESISTANCE,30,1); }
            case "slime" -> { add(e,StatusEffects.JUMP_BOOST,35,3);add(e,StatusEffects.SLOW_FALLING,40,0); }
            case "magma_cube" -> { add(e,StatusEffects.JUMP_BOOST,35,3);add(e,StatusEffects.FIRE_RESISTANCE,40,0);add(e,FIRE_TOUCH,30,0); }
            case "shulker" -> { add(e,StatusEffects.LEVITATION,5,0);add(e,StatusEffects.SLOW_FALLING,35,0);add(e,StatusEffects.RESISTANCE,30,1); }
            case "breeze" -> { add(e,StatusEffects.JUMP_BOOST,30,4);add(e,StatusEffects.SLOW_FALLING,35,0);add(e,StatusEffects.WIND_CHARGED,30,0); }
            case "guardian", "elder_guardian" -> { add(e,StatusEffects.WATER_BREATHING,45,0);add(e,THORNS,35,0);add(e,StatusEffects.CONDUIT_POWER,35,0); }
            case "dolphin" -> { add(e,StatusEffects.DOLPHINS_GRACE,40,0);add(e,StatusEffects.WATER_BREATHING,40,0); }
            case "squid" -> { add(e,StatusEffects.WATER_BREATHING,40,0);add(e,StatusEffects.INVISIBILITY,25,0); }
            case "glow_squid" -> { add(e,StatusEffects.WATER_BREATHING,40,0);add(e,StatusEffects.NIGHT_VISION,40,0);add(e,ECHO,25,0); }
            case "pufferfish" -> { add(e,StatusEffects.WATER_BREATHING,40,0);add(e,VENOM_TOUCH,30,0);add(e,THORNS,30,0); }
            case "axolotl" -> { add(e,StatusEffects.WATER_BREATHING,40,0);add(e,StatusEffects.REGENERATION,12,1); }
            case "turtle" -> { add(e,StatusEffects.WATER_BREATHING,45,0);add(e,StatusEffects.RESISTANCE,35,1); }
            case "cod", "salmon", "tropical_fish", "tadpole" -> { add(e,StatusEffects.WATER_BREATHING,40,0);add(e,StatusEffects.DOLPHINS_GRACE,20,0); }
            case "drowned" -> { add(e,StatusEffects.WATER_BREATHING,40,0);add(e,StatusEffects.STRENGTH,30,0); }
            case "strider" -> { add(e,StatusEffects.FIRE_RESISTANCE,45,0);add(e,StatusEffects.SPEED,30,0); }
            case "wither" -> { add(e,FLIGHT,20,0);add(e,WITHER_TOUCH,35,0);add(e,StatusEffects.RESISTANCE,35,1); }
            case "wither_skeleton" -> { add(e,WITHER_TOUCH,30,0);add(e,StatusEffects.STRENGTH,30,0);add(e,StatusEffects.FIRE_RESISTANCE,30,0); }
            case "ender_dragon" -> { add(e,FLIGHT,30,0);add(e,StatusEffects.STRENGTH,40,1);add(e,StatusEffects.RESISTANCE,40,1); }
            case "warden" -> { add(e,ECHO,40,0);add(e,StatusEffects.STRENGTH,40,1);add(e,StatusEffects.RESISTANCE,40,1);add(e,StatusEffects.NIGHT_VISION,40,0); }
            case "witch" -> { add(e,StatusEffects.FIRE_RESISTANCE,35,0);add(e,StatusEffects.WATER_BREATHING,35,0);add(e,StatusEffects.REGENERATION,12,1); }
            case "evoker", "illusioner" -> { add(e,StatusEffects.INVISIBILITY,30,0);add(e,StatusEffects.ABSORPTION,35,1);add(e,StatusEffects.LUCK,35,0); }
            case "silverfish" -> { add(e,StatusEffects.HASTE,35,1);add(e,StatusEffects.SPEED,30,0); }
            case "goat" -> {add(e,RAM,35,0);add(e,StatusEffects.JUMP_BOOST,35,2);}
            case "rabbit", "frog" -> { add(e,StatusEffects.JUMP_BOOST,35,2);add(e,StatusEffects.SLOW_FALLING,40,0); }
            case "horse", "donkey", "mule", "skeleton_horse", "zombie_horse", "camel" -> { add(e,StatusEffects.SPEED,35,1);add(e,StatusEffects.JUMP_BOOST,35,1); }
            case "cat", "ocelot", "fox" -> {add(e,FELINE_WARD,30,0); add(e,StatusEffects.NIGHT_VISION,40,0);add(e,StatusEffects.SPEED,35,1);add(e,StatusEffects.SLOW_FALLING,40,0); }
            case "wolf" -> { add(e,StatusEffects.SPEED,35,1);add(e,StatusEffects.STRENGTH,30,0); }
            case "polar_bear", "panda", "hoglin", "zoglin", "ravager", "iron_golem", "giant" -> { add(e,StatusEffects.STRENGTH,35,1);add(e,StatusEffects.RESISTANCE,35,0); }
            case "armadillo" -> { add(e,StatusEffects.RESISTANCE,40,1);add(e,StatusEffects.ABSORPTION,35,0); }
            case "llama", "trader_llama" -> { add(e,SPIT,30,0);add(e,THORNS,30,0);add(e,StatusEffects.SPEED,30,0); }
            case "snow_golem" -> { add(e,SNOW_THROW,30,0);add(e,StatusEffects.RESISTANCE,30,0);add(e,StatusEffects.SLOW_FALLING,30,0); }
            case "sniffer" -> {add(e,PROSPECTING,40,0);add(e,StatusEffects.LUCK,40,0);}
            case "creaking" -> {add(e,ROOTED_GUARD,30,0);add(e,StatusEffects.NIGHT_VISION,30,0);}
            case "copper_golem" -> { add(e,StatusEffects.HASTE,35,1);add(e,StatusEffects.RESISTANCE,30,0); }
            case "villager", "wandering_trader" -> { add(e,StatusEffects.HERO_OF_THE_VILLAGE,45,0);add(e,StatusEffects.LUCK,45,0); }
            case "mooshroom" -> { add(e,StatusEffects.SATURATION,2,0);add(e,StatusEffects.HEALTH_BOOST,35,0); }
            case "cow", "pig", "sheep" -> { add(e,StatusEffects.ABSORPTION,30,0); }
            case "zombie", "husk", "zombie_villager", "skeleton", "stray", "bogged", "piglin", "piglin_brute", "zombified_piglin", "pillager", "vindicator" -> add(e,StatusEffects.STRENGTH,30,0);
            default -> add(e,StatusEffects.RESISTANCE,25,0);
        }
        return e;
    }

    /** Ability summary is derived from the actual effects, so newly registered species cannot get stale descriptions. */
    public static String description(String species) {
        List<String> parts=new ArrayList<>();
        for (StatusEffectInstance effect:effects(species)) {
            var key=Registries.STATUS_EFFECT.getId(effect.getEffectType().value()).getPath();
            String label=switch(key) {
                case "flight" -> "短时飞行（双击跳跃）";
                case "climbing" -> "蜘蛛攀墙（贴墙前进）";
                case "blink" -> "末影闪现（按下潜行，4秒冷却）";
                case "creeper_power" -> "蓄爆（按下潜行，4秒冷却；不破坏方块）";
                case "prospecting" -> "嗅探（6格内矿石显示晶光）";
                case "rooted_guard" -> "扎根防御（潜行时抗性IV）";
                case "spit" -> "羊驼吐息（按下潜行）";
                case "snow_throw" -> "雪傀儡投雪（按下潜行）";
                case "ram" -> "山羊冲撞（近战击退）";
                case "feline_ward" -> "猫科威慑（驱离附近苦力怕与幻翼）";
                case "echo" -> "回声探测（标记24格内敌怪）";
                case "thorns" -> "反伤"; case "fire_touch" -> "烈焰附身（近战点燃）";
                case "venom_touch" -> "毒刺（近战附毒）";case "wither_touch" -> "凋零之触";
                default -> null;
            };
            if (label!=null)parts.add(label+" "+effect.getDuration()/20+"秒");
        }
        return String.join("；",parts);
    }

    /** Serialize original permissions even if an autosave/crash happens during temporary flight. */
    public static net.minecraft.entity.player.PlayerAbilities.Packed beforeSave(net.minecraft.entity.player.PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity) || player.isCreative() || player.isSpectator())return null;
        FlightState state=FLIGHTS.get(player.getUuid());if(state==null)return null;
        var abilities=player.getAbilities();var snapshot=abilities.pack();
        abilities.allowFlying=state.allowed();abilities.setFlySpeed(state.speed());
        if(!state.allowed())abilities.flying=false;
        return snapshot;
    }
    public static boolean isMelee(net.minecraft.entity.damage.DamageSource source) {
        return source.isOf(net.minecraft.entity.damage.DamageTypes.PLAYER_ATTACK)
            || source.isOf(net.minecraft.entity.damage.DamageTypes.MOB_ATTACK)
            || source.isOf(net.minecraft.entity.damage.DamageTypes.MOB_ATTACK_NO_AGGRO);
    }
    public static void updateFlight(ServerPlayerEntity player) {
        UUID id=player.getUuid();
        if (player.isCreative() || player.isSpectator()) { FLIGHTS.remove(id);return; }
        var abilities=player.getAbilities();
        if (player.isAlive() && player.hasStatusEffect(FLIGHT)) {
            FLIGHTS.computeIfAbsent(id,k->new FlightState(abilities.allowFlying,abilities.getFlySpeed()));
            if (!abilities.allowFlying) { abilities.allowFlying=true;player.sendAbilitiesUpdate(); }
        } else restoreFlight(player,true);
    }
    public static void restoreFlight(ServerPlayerEntity player,boolean landing) {
        FlightState previous=FLIGHTS.remove(player.getUuid());
        if(previous==null || player.isCreative() || player.isSpectator())return;
        var a=player.getAbilities();a.allowFlying=previous.allowed();a.setFlySpeed(previous.speed());
        if (!a.allowFlying) a.flying=false;
        player.sendAbilitiesUpdate();
        if(landing && player.isAlive() && !previous.allowed())player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,200,0));
    }
    private static void tick(ServerPlayerEntity p) {
        updateFlight(p);
        if(!p.isAlive()){SNEAKING.remove(p.getUuid());LAST_ACTION.remove(p.getUuid());return;}
        if(p.hasStatusEffect(ROOTED_GUARD) && p.isSneaking() && p.age%20==0)
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,25,3));
        if(p.hasStatusEffect(PROSPECTING) && p.age%40==0) {
            var w=p.getEntityWorld();int shown=0;net.minecraft.util.math.BlockPos nearest=null;double nearestDistance=Double.MAX_VALUE;
            for(int x=-6;x<=6&&shown<12;x++)for(int y=-4;y<=4&&shown<12;y++)for(int z=-6;z<=6&&shown<12;z++) {
                var pos=p.getBlockPos().add(x,y,z);if(!w.isChunkLoaded(pos))continue;
                var state=w.getBlockState(pos);
                if(state.isIn(net.minecraft.registry.tag.BlockTags.DIAMOND_ORES) || state.isIn(net.minecraft.registry.tag.BlockTags.IRON_ORES) || state.isIn(net.minecraft.registry.tag.BlockTags.GOLD_ORES) || state.isIn(net.minecraft.registry.tag.BlockTags.EMERALD_ORES)) {
                    if(pos.getSquaredDistance(p.getBlockPos())<nearestDistance){nearest=pos;nearestDistance=pos.getSquaredDistance(p.getBlockPos());}
                    w.spawnParticles(net.minecraft.particle.ParticleTypes.END_ROD,pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,3,.15,.2,.15,.01);shown++;
                }
            }
            if(nearest!=null)p.sendMessage(net.minecraft.text.Text.literal("嗅到矿石："+nearest.getX()+", "+nearest.getY()+", "+nearest.getZ()),true);
        }
        if(p.hasStatusEffect(FELINE_WARD) && p.age%10==0) {
            for(MobEntity mob:p.getEntityWorld().getEntitiesByClass(MobEntity.class,p.getBoundingBox().expand(8),m->m instanceof net.minecraft.entity.mob.CreeperEntity || m instanceof net.minecraft.entity.mob.PhantomEntity)) {
                Vec3d away=mob.getEntityPos().subtract(p.getEntityPos()).normalize().multiply(.32);
                mob.setTarget(null);mob.addVelocity(away.x,.06,away.z);
                if(mob instanceof net.minecraft.entity.mob.CreeperEntity creeper)creeper.setFuseSpeed(-1);
            }
        }
        if(p.hasStatusEffect(ECHO) && p.age%40==0) {
            var w=p.getEntityWorld();
            for (MobEntity m:w.getEntitiesByClass(MobEntity.class,p.getBoundingBox().expand(24),m->m instanceof net.minecraft.entity.mob.HostileEntity || m.getTarget()==p))
                m.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,60,0));
        }
        boolean sneak=p.isSneaking();boolean pressed=sneak&&!SNEAKING.getOrDefault(p.getUuid(),false);SNEAKING.put(p.getUuid(),sneak);
        long now=p.getEntityWorld().getTime();
        int cooldown=p.hasStatusEffect(SPIT)||p.hasStatusEffect(SNOW_THROW)?20:80;
        if(!pressed || now-LAST_ACTION.getOrDefault(p.getUuid(),-1000L)<cooldown)return;
        if(p.hasStatusEffect(SPIT) || p.hasStatusEffect(SNOW_THROW)) {
            net.minecraft.entity.projectile.ProjectileEntity projectile=p.hasStatusEffect(SPIT)?
                new net.minecraft.entity.projectile.LlamaSpitEntity(EntityType.LLAMA_SPIT,p.getEntityWorld()):
                new net.minecraft.entity.projectile.thrown.SnowballEntity(p.getEntityWorld(),p,new net.minecraft.item.ItemStack(net.minecraft.item.Items.SNOWBALL));
            projectile.setOwner(p);projectile.setPosition(p.getX(),p.getEyeY()-.1,p.getZ());
            projectile.setVelocity(p,p.getPitch(),p.getYaw(),0,1.5F,.5F);
            p.getEntityWorld().spawnEntity(projectile);LAST_ACTION.put(p.getUuid(),now);return;
        }
        if(p.hasStatusEffect(BLINK)) {
            Vec3d direction=p.getRotationVec(1).normalize();
            // Search back from eight blocks; teleport requires a collision-free destination and floor.
            for(int distance=8;distance>=2;distance--) {
                Vec3d destination=p.getEntityPos().add(direction.multiply(distance));
                if(p.teleport(destination.x,destination.y,destination.z,true)) {LAST_ACTION.put(p.getUuid(),now);break;}
            }
        } else if(p.hasStatusEffect(CREEPER_POWER)) {
            LAST_ACTION.put(p.getUuid(),now);
            p.getEntityWorld().createExplosion(p,p.getX(),p.getY()+.5,p.getZ(),2.0F,World.ExplosionSourceType.NONE);
        }
    }
    public static void install() {
        ServerTickEvents.END_SERVER_TICK.register(s->s.getPlayerManager().getPlayerList().forEach(EssenceAbilities::tick));
        ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->{
            restoreFlight(handler.player,false);SNEAKING.remove(handler.player.getUuid());LAST_ACTION.remove(handler.player.getUuid());
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(s->s.getPlayerManager().getPlayerList().forEach(p->restoreFlight(p,false)));
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity,source,amount)->
            !(entity.hasStatusEffect(CREEPER_POWER) && source.getAttacker()==entity && source.isIn(net.minecraft.registry.tag.DamageTypeTags.IS_EXPLOSION)));
        ServerLivingEntityEvents.AFTER_DAMAGE.register((target,source,base,damage,blocked)->{
            if(blocked || damage<=0 || reflecting || !(target.getEntityWorld() instanceof ServerWorld world))return;
            if(!(source.getAttacker() instanceof LivingEntity attacker) || attacker==target)return;
            if(source.getSource()==attacker && isMelee(source)) {
                if(attacker.hasStatusEffect(RAM)) {
                    Vec3d direction=target.getEntityPos().subtract(attacker.getEntityPos()).normalize();
                    target.addVelocity(direction.x*.8,.4,direction.z*.8);
                }
                if(attacker.hasStatusEffect(FIRE_TOUCH))target.setOnFireFor(5);
                if(attacker.hasStatusEffect(VENOM_TOUCH))target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON,100,0),attacker);
                if(attacker.hasStatusEffect(WITHER_TOUCH))target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER,100,0),attacker);
            }
            if(target.hasStatusEffect(THORNS)) {
                reflecting=true;
                try {attacker.damage(world,world.getDamageSources().thorns(target),2F);}finally {reflecting=false;}
            }
        });
    }
}
