package com.statboost;

import com.statboost.net.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StatEvents {
    private void refresh(net.minecraft.world.entity.player.Player player) {
        if (player instanceof ServerPlayer sp) {
            StatManager.apply(sp);
            ModNetwork.sync(sp);
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) { refresh(e.getEntity()); }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) { refresh(e.getEntity()); }

    @SubscribeEvent
    public void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) { refresh(e.getEntity()); }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone e) {
        var old = e.getOriginal().getPersistentData();
        if (old.contains(StatManager.TAG)) {
            e.getEntity().getPersistentData().put(StatManager.TAG, old.getCompound(StatManager.TAG).copy());
        }
    }
}
