package com.statboost;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;

public enum StatType {
    STRENGTH("strength", "Sức mạnh", "minecraft:generic.attack_damage",
            AttributeModifier.Operation.MULTIPLY_BASE, 0.04, "7f1b0c11-0000-4a00-9000-000000000001"),
    HEALTH("health", "Máu", "minecraft:generic.max_health",
            AttributeModifier.Operation.MULTIPLY_BASE, 0.04, "7f1b0c11-0000-4a00-9000-000000000002"),
    SPEED("speed", "Tốc độ", "minecraft:generic.movement_speed",
            AttributeModifier.Operation.MULTIPLY_BASE, 0.04, "7f1b0c11-0000-4a00-9000-000000000003"),
    SPELL_POWER("spell_power", "Sức mạnh phép thuật", "irons_spellbooks:spell_power",
            AttributeModifier.Operation.MULTIPLY_BASE, 0.04, "7f1b0c11-0000-4a00-9000-000000000004");

    public final String key, display, defaultAttribute;
    public final AttributeModifier.Operation operation;
    public final double defaultPerLevel;
    public final UUID uuid;

    StatType(String key, String display, String attr, AttributeModifier.Operation op, double perLevel, String uuid) {
        this.key = key; this.display = display; this.defaultAttribute = attr;
        this.operation = op; this.defaultPerLevel = perLevel; this.uuid = UUID.fromString(uuid);
    }
}
