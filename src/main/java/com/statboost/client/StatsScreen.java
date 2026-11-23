package com.statboost.client;

import com.statboost.Config;
import com.statboost.StatType;
import com.statboost.net.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class StatsScreen extends Screen {
    private static final int W = 300, ROW = 30;

    public StatsScreen() { super(Component.literal("Bảng chỉ số")); }

    private int left() { return (width - W) / 2; }
    private int top() { return height / 2 - 70; }

    @Override
    protected void init() {
        for (StatType t : StatType.values()) {
            int y = top() + 25 + t.ordinal() * ROW;
            addRenderableWidget(Button.builder(Component.literal("+"),
                    b -> ModNetwork.CHANNEL.sendToServer(new ModNetwork.UpgradePacket(t.ordinal())))
                    .bounds(left() + W - 24, y, 20, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.fill(left() - 6, top() - 6, left() + W + 6, top() + 25 + StatType.values().length * ROW + 24, 0xC0101018);
        g.drawCenteredString(font, "Bảng chỉ số", width / 2, top() + 4, 0xFFD700);

        int xp = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.experienceLevel : 0;
        for (StatType t : StatType.values()) {
            int y = top() + 25 + t.ordinal() * ROW;
            int lvl = ModNetwork.clientLevels[t.ordinal()];
            int max = Config.MAX_LEVEL.get();
            double per = Config.PER_LEVEL.get(t).get();
            String bonus = t.operation == net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
                    ? String.format("+%.1f", per * lvl) : String.format("+%.0f%%", per * lvl * 100);
            g.drawString(font, t.display + "  " + lvl + "/" + max, left() + 4, y, 0xFFFFFF);
            String cost = lvl >= max ? "TỐI ĐA" : "Giá: " + Config.cost(lvl) + " level";
            g.drawString(font, bonus + "   " + cost, left() + 4, y + 11, lvl < max && xp < Config.cost(lvl) ? 0xFF6666 : 0xAAAAAA);
        }
        g.drawString(font, "Level kinh nghiệm hiện có: " + xp, left() + 4, top() + 25 + StatType.values().length * ROW + 4, 0x80FF80);
        super.render(g, mx, my, pt);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
