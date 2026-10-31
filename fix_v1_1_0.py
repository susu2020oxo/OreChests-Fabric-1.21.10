from pathlib import Path

main_path = Path("src/main/java/com/orechests/OreChestsMod.java")
s = main_path.read_text(encoding="utf-8")

replacements = [
    ("chest.setCustomName(Text.literal(kind.chestName));", ""),
    ("fillLoot(chest, kind);", "fillLoot(world, chest, kind);"),
    ("private static void fillLoot(ChestBlockEntity chest, OreKind kind)", "private static void fillLoot(ServerWorld world, ChestBlockEntity chest, OreKind kind)"),
    ("enchanted(ModItems.IRON_HAMMER,", "enchanted(world, ModItems.IRON_HAMMER,"),
    ("enchanted(ModItems.NETHERITE_MULTISHOT_BOW,", "enchanted(world, ModItems.NETHERITE_MULTISHOT_BOW,"),
    ("addEnchantment(bow,", "addEnchantment(world, bow,"),
    ("private static ItemStack enchanted(Item item,", "private static ItemStack enchanted(ServerWorld world, Item item,"),
    ("addEnchantment(stack, enchantment, level);", "addEnchantment(world, stack, enchantment, level);"),
    ("private static void addEnchantment(ItemStack stack,", "private static void addEnchantment(ServerWorld world, ItemStack stack,"),
    ("Registries.ENCHANTMENT.getEntry(enchantment)", "world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(enchantment)"),
    ("if (!world.isClient() && player.getStackInHand(hand).isOf(ModItems.IRON_HAMMER))", "if (!world.isClient() && world instanceof ServerWorld serverWorld && player.getStackInHand(hand).isOf(ModItems.IRON_HAMMER))"),
    ("living.damage(world.getDamageSources().playerAttack(player), 20.0F);", "living.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), 20.0F);"),
    ("import net.minecraft.registry.Registries;", "import net.minecraft.registry.RegistryKeys;"),
]

for old, new in replacements:
    if old not in s:
        raise SystemExit("Expected source text not found: " + old)
    s = s.replace(old, new)

main_path.write_text(s, encoding="utf-8")

egg_path = Path("src/main/java/com/orechests/item/IronGolemEggItem.java")
egg = egg_path.read_text(encoding="utf-8")
if "SpawnReason.SPAWN_EGG" not in egg:
    raise SystemExit("Expected spawn reason not found")
egg_path.write_text(egg.replace("SpawnReason.SPAWN_EGG", "SpawnReason.SPAWN_ITEM_USE"), encoding="utf-8")

print("Applied Minecraft 1.21.10 API compatibility fixes.")
