package me.thepond.soltribes.data;

import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TribesData extends PersistentState {

    private final List<Tribe> tribes;

    public TribesData() {
        this.tribes = new ArrayList<>();
    }

    public void addOrUpdateTribe(Tribe tribe) {
        tribes.removeIf(t -> t.getTribeId().equals(tribe.getTribeId()));
        tribes.add(tribe);
        setDirty(true);
    }

    public void removeTribe(Tribe tribe) {
        tribes.removeIf(t -> t.getTribeId().equals(tribe.getTribeId()));
        setDirty(true);
    }

    public void removeTribe(UUID tribeId) {
        tribes.removeIf(t -> t.getTribeId().equals(tribeId));
        setDirty(true);
    }

    public Tribe getTribe(UUID tribeId) {
        for (Tribe tribe : tribes) {
            if (tribe.isDisbanded()) continue;
            if (tribe.getTribeId().equals(tribeId)) {
                return tribe;
            }
        }
        return null;
    }

    public Tribe getNearbyTribeFromSettlement(BlockPos pos) {
        for (Tribe tribe : tribes) {
            if (tribe.isDisbanded()) continue;
            for (TribeSettlement tribeSettlement : tribe.getTribeSettlements()) {
                if (tribeSettlement.getTribeSettlementPosition().isWithinDistance(pos, 200)) {
                    return tribe;
                }
            }
        }
        return null;
    }

    public List<Tribe> getNearbyTribesFromSettlement(BlockPos pos) {
        List<Tribe> nearbyTribes = new ArrayList<>();
        for (Tribe tribe : tribes) {
            if (tribe.isDisbanded()) continue;
            for (TribeSettlement tribeSettlement : tribe.getTribeSettlements()) {
                if (tribeSettlement.getTribeSettlementPosition().isWithinDistance(pos, 200)) {
                    nearbyTribes.add(tribe);
                }
            }
        }
        return nearbyTribes;
    }

    public Tribe getTribeFromPlayer(UUID playerId) {
        for (Tribe tribe : tribes) {
            if (tribe.isDisbanded()) continue;
            if (tribe.getTribeMembers().stream().anyMatch(member -> member.getTribeMemberUUID().equals(playerId))) {
                return tribe;
            }
        }
        return null;
    }

    public List<Tribe> getTribes() {
        return tribes;
    }

    public static TribesData fromNbt(NbtCompound nbt) {
        TribesData tribesData = new TribesData();
        NbtList tribeList = nbt.getList("tribes_tribes", 10);
        for (int i = 0; i < tribeList.size(); i++) {
            tribesData.tribes.add(Tribe.fromNbt(tribeList.getCompound(i)));
        }
        return tribesData;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList tribeList = new NbtList();
        for (Tribe tribe : tribes) {
            tribeList.add(tribe.toNbt());
        }
        nbt.put("tribes_tribes", tribeList);
        return nbt;
    }
}
