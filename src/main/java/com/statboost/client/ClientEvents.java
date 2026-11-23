package com.statboost.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.statboost.StatBoost;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {
    public static final KeyMapping OPEN = new KeyMapping("key.statboost.open",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.statboost");

    @Mod.EventBusSubscriber(modid = StatBoost.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) { e.register(OPEN); }
    }

    @Mod.EventBusSubscriber(modid = StatBoost.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            while (OPEN.consumeClick()) {
                if (mc.player != null && mc.screen == null) mc.setScreen(new StatsScreen());
            }
        }
    }
}
