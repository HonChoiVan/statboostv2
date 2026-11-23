package com.statboost.net;

import com.statboost.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ModNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(StatBoost.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    /** Dữ liệu cấp hiện tại phía client (chỉ là mảng tĩnh, an toàn trên server). */
    public static int[] clientLevels = new int[StatType.values().length];

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, UpgradePacket.class, UpgradePacket::encode, UpgradePacket::decode, UpgradePacket::handle);
        CHANNEL.registerMessage(id++, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle);
    }

    public static void sync(ServerPlayer p) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new SyncPacket(StatManager.getAll(p)));
    }

    // ---- Client -> Server: xin nâng cấp ----
    public static class UpgradePacket {
        final int stat;
        public UpgradePacket(int stat) { this.stat = stat; }
        static void encode(UpgradePacket m, FriendlyByteBuf buf) { buf.writeVarInt(m.stat); }
        static UpgradePacket decode(FriendlyByteBuf buf) { return new UpgradePacket(buf.readVarInt()); }

        static void handle(UpgradePacket m, Supplier<NetworkEvent.Context> sup) {
            NetworkEvent.Context ctx = sup.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                StatType[] all = StatType.values();
                if (p == null || m.stat < 0 || m.stat >= all.length) return;
                StatType t = all[m.stat];
                int lvl = StatManager.get(p, t);
                if (lvl >= Config.MAX_LEVEL.get()) return;
                int cost = Config.cost(lvl);
                if (!p.getAbilities().instabuild) {
                    if (p.experienceLevel < cost) return;
                    p.giveExperienceLevels(-cost);
                }
                float oldMax = p.getMaxHealth();
                StatManager.set(p, t, lvl + 1);
                StatManager.apply(p);
                if (t == StatType.HEALTH) p.heal(Math.max(0f, p.getMaxHealth() - oldMax));
                sync(p);
            });
            ctx.setPacketHandled(true);
        }
    }

    // ---- Server -> Client: đồng bộ cấp ----
    public static class SyncPacket {
        final int[] levels;
        public SyncPacket(int[] levels) { this.levels = levels; }
        static void encode(SyncPacket m, FriendlyByteBuf buf) {
            buf.writeVarInt(m.levels.length);
            for (int l : m.levels) buf.writeVarInt(l);
        }
        static SyncPacket decode(FriendlyByteBuf buf) {
            int n = buf.readVarInt();
            int[] r = new int[n];
            for (int i = 0; i < n; i++) r[i] = buf.readVarInt();
            return new SyncPacket(r);
        }
        static void handle(SyncPacket m, Supplier<NetworkEvent.Context> sup) {
            NetworkEvent.Context ctx = sup.get();
            ctx.enqueueWork(() -> {
                int[] dst = new int[StatType.values().length];
                System.arraycopy(m.levels, 0, dst, 0, Math.min(dst.length, m.levels.length));
                clientLevels = dst;
            });
            ctx.setPacketHandled(true);
        }
    }
}
