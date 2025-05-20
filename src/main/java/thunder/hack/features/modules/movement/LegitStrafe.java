package thunder.hack.features.modules.movement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

import java.util.Comparator;

public class LegitStrafe extends Module {
    private final Setting<Float> distance = new Setting<>("Distance", 2.0f, 1.0f, 3.0f);
    private final Setting<Boolean> strafeRight = new Setting<>("StrafeRight", false);

    private State state = State.APPROACH;

    public LegitStrafe() {
        super("LegitStrafe", Category.MOVEMENT);
    }

    private enum State {
        APPROACH,
        RETREAT,
        STRAFE
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        PlayerEntity target = mc.world.getPlayers().stream()
                .filter(p -> p != mc.player && !p.isDead())
                .min(Comparator.comparingDouble(mc.player::distanceTo))
                .orElse(null);

        if (target == null) return;

        double dist = mc.player.distanceTo(target);

        switch (state) {
            case APPROACH:
                if (dist > 1.1) {
                    moveToward(target);
                } else {
                    state = State.RETREAT;
                }
                break;

            case RETREAT:
                if (dist < 1.8) {
                    moveAway(target);
                } else {
                    state = State.STRAFE;
                }
                break;

            case STRAFE:
                if (dist > distance.getValue()) {
                    state = State.APPROACH;
                } else {
                    strafeAround(target);
                }
                break;
        }
    }

    private void moveToward(PlayerEntity target) {
        float yaw = getYawToEntity(target);
        setMotionInDirection(yaw);
    }

    private void moveAway(PlayerEntity target) {
        float yaw = getYawToEntity(target) + 180;
        setMotionInDirection(yaw);
    }

    private void strafeAround(PlayerEntity target) {
        float yaw = getYawToEntity(target) + (strafeRight.getValue() ? 90 : -90);
        setMotionInDirection(yaw);
    }

    private void setMotionInDirection(float yaw) {
        double rad = Math.toRadians(yaw);
        double x = -Math.sin(rad) * 0.2;
        double z = Math.cos(rad) * 0.2;
        mc.player.setVelocity(x, mc.player.getVelocity().y, z);
    }

    private float getYawToEntity(PlayerEntity target) {
        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
    }
}
