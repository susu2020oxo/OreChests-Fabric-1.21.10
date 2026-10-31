package com.susu.orechests;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.EquipmentSlotGroup;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public final class OreChestsMod implements ModInitializer {
    public static final String MOD_ID = "orechests";

    public static final String HAMMER_NAME = "铁锤";
    public static final String GOLEM_EGG_NAME = "铁傀儡生成蛋";
    public static final String PHONE_NAME = "猪人抢夺器（手机）";
    public static final String ONE_SHOT_AXE_NAME = "秒人斧";
    public static final String DIAMOND_WAND_NAME = "钻石法杖";
    public static final String SUPER_CHESTPLATE_NAME = "超级钻石胸甲";
    public static final String COAL_SWORD_NAME = "煤炭剑";
    public static final String BLACK_BALL_NAME = "黑色禁锢球";
    public static final String NETHERITE_BOW_NAME = "下界合金弓";

    private static final Map<String, SimpleContainer> ORE_INVENTORIES = new HashMap<>();

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register(OreChestsMod::onUseBlock);
        UseEntityCallback.EVENT.register(OreChestsMod::onUseEntity);
        AttackEntityCallback.EVENT.register(OreChestsMod::onAttackEntity);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tickPlayer(player);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ORE_INVENTORIES.clear());
    }

    public static boolean isNamed(ItemStack stack, String name) {
        return stack != null
                && !stack.isEmpty()
                && stack.has(DataComponents.CUSTOM_NAME)
                && stack.getHoverName().getString().equals(name);
    }

    private static InteractionResult onUseBlock(net.minecraft.world.entity.player.Player player,
                                                  Level level,
                                                  InteractionHand hand,
                                                  BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);

        // The iron golem "egg" is represented by a named vanilla egg.
        if (isNamed(held, GOLEM_EGG_NAME)) {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                BlockPos spawnPos = hit.getBlockPos().relative(hit.getDirection()).above();
                var golem = EntityType.IRON_GOLEM.create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
                if (golem != null) {
                    golem.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                            player.getYRot(), 0.0F);
                    serverLevel.addFreshEntity(golem);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        String oreType = getOreType(state);
        if (oreType == null) {
            return InteractionResult.PASS;
        }

        // SUCCESS on the client sends the interaction to the server, where the menu is opened.
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            String key = level.dimension().location() + ":" + pos.asLong();
            SimpleContainer inventory = ORE_INVENTORIES.computeIfAbsent(
                    key, ignored -> createLoot(serverPlayer.serverLevel(), oreType));

            String title = switch (oreType) {
                case "iron" -> "铁矿宝箱";
                case "gold" -> "金矿宝箱";
                case "diamond" -> "钻石矿宝箱";
                case "coal" -> "煤矿宝箱";
                case "copper" -> "铜矿宝箱";
                case "ancient_debris" -> "远古残骸宝箱";
                case "emerald" -> "绿宝石矿宝箱";
                case "lapis" -> "青金石矿宝箱";
                case "redstone" -> "红石矿宝箱";
                case "quartz" -> "下界石英矿宝箱";
                default -> "矿物宝箱";
            };

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (syncId, playerInventory, menuPlayer) ->
                            ChestMenu.threeRows(syncId, playerInventory, inventory),
                    Component.literal(title)
            ));
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult onUseEntity(net.minecraft.world.entity.player.Player player,
                                                   Level level,
                                                   InteractionHand hand,
                                                   Entity entity,
                                                   EntityHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !(entity instanceof Piglin)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!isNamed(held, PHONE_NAME)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            dropItem(serverLevel, entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    Items.ENDER_PEARL, 60);
            dropItem(serverLevel, entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    Items.OBSIDIAN, 60);
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult onAttackEntity(net.minecraft.world.entity.player.Player player,
                                                     Level level,
                                                     InteractionHand hand,
                                                     Entity entity,
                                                     EntityHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !(entity instanceof LivingEntity target)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        float damage;
        if (isNamed(held, HAMMER_NAME)) {
            damage = 20.0F;
        } else if (isNamed(held, ONE_SHOT_AXE_NAME)) {
            damage = 1000.0F;
        } else if (isNamed(held, COAL_SWORD_NAME)) {
            damage = 5.0F;
        } else {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer) {
            target.hurtServer(serverLevel, serverLevel.damageSources().playerAttack(player), damage);
            held.hurtAndBreak(1, serverLevel, serverPlayer, brokenItem -> { });
        }

        return InteractionResult.SUCCESS;
    }

    private static void tickPlayer(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();

        if (isNamed(held, COAL_SWORD_NAME)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
        } else {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (isNamed(chest, SUPER_CHESTPLATE_NAME)) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 220, 3, true, false));
        }

        if (isNamed(held, DIAMOND_WAND_NAME) && player.onGround()) {
            BlockPos underPlayer = BlockPos.containing(player.getX(), player.getY() - 0.05, player.getZ());
            if (!player.serverLevel().getBlockState(underPlayer).is(Blocks.DIAMOND_BLOCK)) {
                player.serverLevel().setBlock(underPlayer, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
            }
        }
    }

    private static SimpleContainer createLoot(ServerLevel level, String type) {
        SimpleContainer inventory = new SimpleContainer(27);

        switch (type) {
            case "iron" -> {
                inventory.setItem(0, new ItemStack(Items.RAW_IRON, 2));
                addRandom(inventory, Items.IRON_INGOT, random(level, 20, 60));
                if (level.getRandom().nextFloat() < 0.70F) {
                    ItemStack hammer = named(Items.IRON_AXE, HAMMER_NAME);
                    enchant(level, hammer, Enchantments.SWEEPING_EDGE, 20);
                    inventory.addItem(hammer);
                }
                inventory.addItem(named(Items.EGG, GOLEM_EGG_NAME));
            }
            case "gold" -> {
                inventory.setItem(0, new ItemStack(Items.RAW_GOLD, 2));
                addRandom(inventory, Items.GOLD_INGOT, random(level, 30, 60));
                inventory.addItem(named(Items.MAP, PHONE_NAME));
                if (level.getRandom().nextFloat() < 0.40F) {
                    inventory.addItem(namedWithDurability(Items.NETHERITE_AXE, ONE_SHOT_AXE_NAME, 10));
                }
            }
            case "diamond" -> {
                inventory.setItem(0, new ItemStack(Items.DIAMOND, 1));
                addRandom(inventory, Items.DIAMOND, random(level, 30, 60));
                inventory.addItem(named(Items.STICK, DIAMOND_WAND_NAME));
                ItemStack chestplate = named(Items.DIAMOND_CHESTPLATE, SUPER_CHESTPLATE_NAME);
                ItemAttributeModifiers modifiers = chestplate.getOrDefault(
                        DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                chestplate.set(DataComponents.ATTRIBUTE_MODIFIERS,
                        modifiers.withModifierAdded(
                                Attributes.ARMOR,
                                new AttributeModifier(
                                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "super_diamond_armor"),
                                        12.0,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.CHEST));
                inventory.addItem(chestplate);
            }
            case "coal" -> {
                inventory.setItem(0, new ItemStack(Items.COAL, 1));
                addRandom(inventory, Items.COAL, random(level, 20, 100));
                inventory.addItem(named(Items.IRON_SWORD, COAL_SWORD_NAME));
                inventory.addItem(named(Items.SNOWBALL, BLACK_BALL_NAME));
            }
            case "copper" -> {
                inventory.setItem(0, named(Items.PAPER, "铜矿狗都不挖"));
            }
            case "ancient_debris" -> {
                inventory.setItem(0, new ItemStack(Items.ANCIENT_DEBRIS, 1));
                addRandom(inventory, Items.NETHERITE_INGOT, random(level, 5, 10));
                ItemStack bow = namedWithDurability(Items.BOW, NETHERITE_BOW_NAME, 100);
                enchant(level, bow, Enchantments.MULTISHOT, 100);
                enchant(level, bow, Enchantments.INFINITY, 1);
                inventory.addItem(bow);
            }
            case "emerald" -> {
                inventory.setItem(0, new ItemStack(Items.EMERALD, random(level, 8, 16)));
            }
            case "lapis" -> {
                inventory.setItem(0, new ItemStack(Items.LAPIS_LAZULI, random(level, 20, 48)));
            }
            case "redstone" -> {
                inventory.setItem(0, new ItemStack(Items.REDSTONE, random(level, 20, 48)));
            }
            case "quartz" -> {
                inventory.setItem(0, new ItemStack(Items.QUARTZ, random(level, 12, 32)));
            }
            default -> inventory.setItem(0, new ItemStack(Items.COBBLESTONE, 1));
        }

        return inventory;
    }

    private static String getOreType(BlockState state) {
        if (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)) return "iron";
        if (state.is(Blocks.GOLD_ORE) || state.is(Blocks.DEEPSLATE_GOLD_ORE)
                || state.is(Blocks.NETHER_GOLD_ORE)) return "gold";
        if (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)) return "diamond";
        if (state.is(Blocks.COAL_ORE) || state.is(Blocks.DEEPSLATE_COAL_ORE)) return "coal";
        if (state.is(Blocks.COPPER_ORE) || state.is(Blocks.DEEPSLATE_COPPER_ORE)) return "copper";
        if (state.is(Blocks.ANCIENT_DEBRIS)) return "ancient_debris";
        if (state.is(Blocks.EMERALD_ORE) || state.is(Blocks.DEEPSLATE_EMERALD_ORE)) return "emerald";
        if (state.is(Blocks.LAPIS_ORE) || state.is(Blocks.DEEPSLATE_LAPIS_ORE)) return "lapis";
        if (state.is(Blocks.REDSTONE_ORE) || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)) return "redstone";
        if (state.is(Blocks.NETHER_QUARTZ_ORE)) return "quartz";
        return null;
    }

    private static ItemStack named(Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }

    private static ItemStack namedWithDurability(Item item, String name, int durability) {
        ItemStack stack = named(item, name);
        stack.set(DataComponents.MAX_DAMAGE, durability);
        stack.set(DataComponents.DAMAGE, 0);
        return stack;
    }

    private static void enchant(ServerLevel level, ItemStack stack,
                                net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey,
                                int enchantmentLevel) {
        Holder<Enchantment> enchantment = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantmentKey);
        stack.enchant(enchantment, enchantmentLevel);
    }

    private static void addRandom(SimpleContainer inventory, Item item, int count) {
        inventory.addItem(new ItemStack(item, count));
    }

    private static int random(ServerLevel level, int min, int max) {
        return min + level.getRandom().nextInt(max - min + 1);
    }

    private static void dropItem(ServerLevel level, double x, double y, double z, Item item, int count) {
        level.addFreshEntity(new ItemEntity(level, x, y, z, new ItemStack(item, count)));
    }
}
