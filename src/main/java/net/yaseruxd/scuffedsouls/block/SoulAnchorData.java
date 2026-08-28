package net.yaseruxd.scuffedsouls.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoulAnchorData extends SavedData {

    private static final String DATA_KEY = "soul_anchor_xp";
    private static final long EXPIRY_MS = 30L * 60 * 1000; // 30 minutes in milliseconds

    public final Map<Long, Integer> xpMap = new HashMap<>();
    private final Map<Long, UUID> ownerMap = new HashMap<>();
    private final Map<Long, Long> timestampMap = new HashMap<>();

    public static SoulAnchorData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                SoulAnchorData::load,
                SoulAnchorData::new,
                DATA_KEY
        );
    }

    public static void storeXp(Level level, BlockPos pos, int xp, UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        SoulAnchorData data = get(serverLevel);
        long key = pos.asLong();
        data.xpMap.put(key, xp);
        data.ownerMap.put(key, ownerUUID);
        data.timestampMap.put(key, System.currentTimeMillis());
        data.setDirty();
    }

    public static int getXp(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return 0;
        return get(serverLevel).xpMap.getOrDefault(pos.asLong(), 0);
    }

    public static UUID getOwner(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return get(serverLevel).ownerMap.get(pos.asLong());
    }

    public static boolean isExpired(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        Long timestamp = get(serverLevel).timestampMap.get(pos.asLong());
        if (timestamp == null) return true;
        return System.currentTimeMillis() - timestamp > EXPIRY_MS;
    }

    public static void clearXp(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        SoulAnchorData data = get(serverLevel);
        long key = pos.asLong();
        data.xpMap.remove(key);
        data.ownerMap.remove(key);
        data.timestampMap.remove(key);
        data.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag xpTag = new CompoundTag();
        CompoundTag ownerTag = new CompoundTag();
        CompoundTag tsTag = new CompoundTag();
        xpMap.forEach((pos, xp) -> xpTag.putInt(String.valueOf(pos), xp));
        ownerMap.forEach((pos, uuid) -> ownerTag.putString(String.valueOf(pos), uuid.toString()));
        timestampMap.forEach((pos, ts) -> tsTag.putLong(String.valueOf(pos), ts));
        tag.put("xpMap", xpTag);
        tag.put("ownerMap", ownerTag);
        tag.put("timestampMap", tsTag);
        return tag;
    }

    public static SoulAnchorData load(CompoundTag tag) {
        SoulAnchorData data = new SoulAnchorData();
        CompoundTag xpTag = tag.getCompound("xpMap");
        CompoundTag ownerTag = tag.getCompound("ownerMap");
        CompoundTag tsTag = tag.getCompound("timestampMap");
        for (String key : xpTag.getAllKeys()) {
            data.xpMap.put(Long.parseLong(key), xpTag.getInt(key));
        }
        for (String key : ownerTag.getAllKeys()) {
            data.ownerMap.put(Long.parseLong(key), UUID.fromString(ownerTag.getString(key)));
        }
        for (String key : tsTag.getAllKeys()) {
            data.timestampMap.put(Long.parseLong(key), tsTag.getLong(key));
        }
        return data;
    }
}