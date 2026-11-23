package com.statboost;

import com.statboost.net.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(StatBoost.MODID)
public class StatBoost {
    public static final String MODID = "statboost";

    public StatBoost() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        bus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(ModNetwork::register));
        MinecraftForge.EVENT_BUS.register(new StatEvents());
    }
}
