package com.statboost;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class StatManager {
    public static final String TAG = "statboost_data";

    public static int get(Player p, StatType t) {
        return p.getPersistentData().getCompound(TAG).getInt(t.key);
    }

    public static int[] getAll(Player p) {
        int[] r = new int[StatType.values().length];
        for (StatType t : StatType.values()) r[t.ordinal()] = get(p, t);
        return r;
    }

    public static void set(Player p, StatType t, int level) {
        CompoundTag data = p.getPersistentData().getCompound(TAG);
        data.putInt(t.key, level);
        p.getPersistentData().put(TAG, data);
    }

    /** Gắn lại toàn bộ modifier theo cấp đã lưu. */
    public static void apply(ServerPlayer p) {
        for (StatType t : StatType.values()) {
            ResourceLocation id = ResourceLocation.tryParse(Config.ATTRIBUTE.get(t).get());
            if (id == null) continue;
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(id);
            if (attr == null) continue; // mod chưa cài -> bỏ qua
            AttributeInstance inst = p.getAttribute(attr);
            if (inst == null) continue;
            inst.removeModifier(t.uuid);
            int lvl = get(p, t);
            if (lvl > 0) {
                inst.addPermanentModifier(new AttributeModifier(t.uuid, "statboost_" + t.key,
                        Config.PER_LEVEL.get(t).get() * lvl, t.operation));
            }
        }
        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
    }
}
