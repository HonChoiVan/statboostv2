package com.statboost;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.EnumMap;
import java.util.Map;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MAX_LEVEL, BASE_COST, COST_STEP;
    public static final Map<StatType, ForgeConfigSpec.DoubleValue> PER_LEVEL = new EnumMap<>(StatType.class);
    public static final Map<StatType, ForgeConfigSpec.ConfigValue<String>> ATTRIBUTE = new EnumMap<>(StatType.class);

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("general");
        MAX_LEVEL = b.comment("Cấp tối đa của mỗi chỉ số").defineInRange("maxLevel", 50, 1, 1000);
        BASE_COST = b.comment("Số level kinh nghiệm cần cho lần nâng đầu tiên").defineInRange("baseCost", 1, 0, 1000);
        COST_STEP = b.comment("Mỗi cấp đã nâng làm giá tăng thêm bấy nhiêu level").defineInRange("costStep", 1, 0, 1000);
        b.pop();
        for (StatType t : StatType.values()) {
            b.push(t.key);
            ATTRIBUTE.put(t, b.comment("ID attribute (đổi nếu mod addon dùng attribute khác)")
                    .define("attribute", t.defaultAttribute));
            PER_LEVEL.put(t, b.comment("Giá trị cộng thêm mỗi cấp (MULTIPLY_BASE: 0.03 = +3%)")
                    .defineInRange("perLevel", t.defaultPerLevel, 0.0, 1000.0));
            b.pop();
        }
        SPEC = b.build();
    }

    public static int cost(int currentLevel) {
        return BASE_COST.get() + currentLevel * COST_STEP.get();
    }
}
