package thunder.hack.features.modules.combat;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;


import java.util.*;

public class Backtrack extends Module {
    private final Setting<Float> radius = new Setting<>("Radius", 3.0f, 1.0f, 6.0f);
    private final Setting<Integer> trackTime = new Setting<>("TrackTime", 10, 1, 20); // in ticks
    private final Setting<Boolean> render = new Setting<>("Render", true);
    private final Map<UUID, Deque<Vec3d>> trackedPositions = new HashMap<>();

    public Backtrack() {
        super("Backtrack", Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || mc.player.distanceTo(player) > radius.getValue()) continue;

            trackedPositions
                    .computeIfAbsent(player.getUuid(), uuid -> new ArrayDeque<>())
                    .addFirst(player.getPos());

            Deque<Vec3d> positions = trackedPositions.get(player.getUuid());
            while (positions.size() > trackTime.getValue()) {
                positions.removeLast();
            }
        }
    }

    public Vec3d getBacktrackPosition(PlayerEntity player) {
        Deque<Vec3d> positions = trackedPositions.get(player.getUuid());
        return (positions != null && !positions.isEmpty()) ? positions.getLast() : player.getPos();
    }

}
