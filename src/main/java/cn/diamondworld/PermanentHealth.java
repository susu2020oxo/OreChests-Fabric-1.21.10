package cn.diamondworld;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Food health is a persistent attribute, not a removable status effect. */
public final class PermanentHealth {
    public static final Identifier BONUS_ID = Identifier.of(DiamondWorld.ID, "food_health");
    public static final double POINTS_PER_MEAL = 1.0;
    public static final double MAX_HEALTH = 1024.0;
    private PermanentHealth() { }

    public static double bonus(ServerPlayerEntity player) {
        var health = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (health == null) return 0;
        var modifier = health.getModifier(BONUS_ID);
        return modifier == null ? 0 : modifier.value();
    }

    /** Called only after a server player actually consumes food or a cake slice. */
    public static void eat(ServerPlayerEntity player) {
        var health = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (health == null || !player.isAlive()) return;
        double gain = Math.min(POINTS_PER_MEAL, Math.max(0, MAX_HEALTH - health.getValue()));
        if (gain <= 0) {
            player.sendMessage(Text.literal("永久生命上限已达到原版上限 1024 点"), true);
            return;
        }
        double total = bonus(player) + gain;
        health.overwritePersistentModifier(new EntityAttributeModifier(BONUS_ID, total,
            EntityAttributeModifier.Operation.ADD_VALUE));
        player.heal((float) gain);
        player.sendMessage(Text.literal("永久生命上限 +" + number(gain)
            + "（食物累计 +" + number(total) + " 点）"), true);
    }

    private static String number(double value) {
        return value == Math.rint(value) ? Long.toString((long)value) : String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    /** Vanilla saves persistent modifiers automatically; player replacement on death needs an explicit copy. */
    public static void copy(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        double amount = bonus(oldPlayer);
        var health = newPlayer.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (health == null) return;
        if (amount > 0) {
            health.overwritePersistentModifier(new EntityAttributeModifier(BONUS_ID, amount,
                EntityAttributeModifier.Operation.ADD_VALUE));
        } else {
            health.removeModifier(BONUS_ID);
        }
        if (!alive) newPlayer.setHealth(newPlayer.getMaxHealth());
    }

    public static void install() {
        ServerPlayerEvents.COPY_FROM.register(PermanentHealth::copy);
    }
}
