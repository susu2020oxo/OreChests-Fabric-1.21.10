package cn.diamondworld;

import java.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.registry.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.LoggerFactory;

public final class DiamondWorld implements ModInitializer {
    public static final String ID = "diamondworld";
    public static final List<Item> CONTENTS = new ArrayList<>();
    public static final Map<EntityType<?>, Item> ESSENCES = new HashMap<>();
    public static Item fragment;

    private static Item register(String name, Item.Settings settings) {
        Identifier id = Identifier.of(ID, name);
        Item item = Registry.register(Registries.ITEM, id,
            new Item(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        CONTENTS.add(item);
        return item;
    }
    private static Item.Settings food(int nutrition, int seconds, boolean cooked, String species) {
        List<StatusEffectInstance> effects = new ArrayList<>();
        effects.add(new StatusEffectInstance(StatusEffects.REGENERATION, seconds * 20, cooked ? 1 : 0));
        if (Set.of("blaze", "magma_cube", "strider", "ghast").contains(species))
            effects.add(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
        if (Set.of("cod", "salmon", "tropical_fish", "pufferfish", "squid", "glow_squid", "dolphin", "turtle", "axolotl", "guardian", "elder_guardian").contains(species))
            effects.add(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 600, 0));
        if (Set.of("rabbit", "horse", "donkey", "mule", "camel", "chicken").contains(species))
            effects.add(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
        if (Set.of("zombie", "husk", "drowned", "skeleton", "stray", "bogged", "wither_skeleton", "spider", "cave_spider", "creeper", "piglin_brute").contains(species))
            effects.add(new StatusEffectInstance(StatusEffects.STRENGTH, 300, 0));
        if (Set.of("warden", "wither", "ender_dragon", "iron_golem", "ravager").contains(species))
            effects.add(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 0));
        ConsumableComponent consume = ConsumableComponent.builder()
            .consumeEffect(new ApplyEffectsConsumeEffect(effects, 1.0F)).build();
        return new Item.Settings().food(new FoodComponent.Builder().nutrition(nutrition)
            .saturationModifier(0.8F).alwaysEdible().build(), consume);
    }
    @Override public void onInitialize() {
        fragment = register("diamond_fragment", new Item.Settings());
        String[] foods = {"beef", "cooked_beef", "porkchop", "cooked_porkchop", "mutton", "cooked_mutton", "chicken", "cooked_chicken", "rabbit", "cooked_rabbit", "cod", "cooked_cod", "salmon", "cooked_salmon", "tropical_fish", "pufferfish", "rotten_flesh", "spider_eye"};
        for (String food : foods) {
            boolean cooked = food.startsWith("cooked_");
            String species = food.replace("cooked_", "");
            species = switch(species) {case "beef" -> "cow"; case "porkchop" -> "pig"; case "mutton" -> "sheep"; case "rotten_flesh" -> "zombie"; case "spider_eye" -> "spider"; default -> species;};
            register("diamond_" + food, food(cooked ? 10 : 6, cooked ? 8 : 5, cooked, species));
        }
        for (EntityType<?> type : Registries.ENTITY_TYPE) {
            Identifier id = Registries.ENTITY_TYPE.getId(type);
            if (id.getNamespace().equals("minecraft") && net.minecraft.entity.attribute.DefaultAttributeRegistry.hasDefinitionFor(type) && type != EntityType.PLAYER && type != EntityType.ARMOR_STAND)
                ESSENCES.put(type, register(id.getPath() + "_essence", food(4, 5, false, id.getPath())));
        }
        Registry.register(Registries.ITEM_GROUP, Identifier.of(ID, "contents"), FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.diamondworld"))
            .icon(() -> new ItemStack(fragment))
            .entries((context, entries) -> CONTENTS.forEach(entries::add)).build());
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof MobEntity mob) || mob.isBaby()) return;
            if (!(entity.getEntityWorld() instanceof ServerWorld world)) return;
            if (!world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) return;
            Item essence = ESSENCES.get(entity.getType());
            if (essence != null) {
                boolean boss = entity.getType() == EntityType.WITHER || entity.getType() == EntityType.ENDER_DRAGON || entity.getType() == EntityType.WARDEN;
                entity.dropStack(world, new ItemStack(essence, boss ? 12 : 1 + world.random.nextInt(2)));
            }
        });
        Verification.install();
        Preview.install();
        LoggerFactory.getLogger(ID).info("Diamond World initialized: {} items, {} species", CONTENTS.size(), ESSENCES.size());
    }
}
