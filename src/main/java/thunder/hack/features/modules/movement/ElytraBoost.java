package thunder.hack.features.modules.movement;

import thunder.hack.features.modules.Module;

import thunder.hack.setting.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class ElytraBoost extends Module {

    private final Setting<Float> boostValue = new Setting<>("Boost", 33f, 33f, 40f);
    private final Setting<Float> multiplier = new Setting<>("Multiplier", 1.1f, 1.1f, 2.0f);

    private boolean boostPending = false;
    private int delayTicks = 0;

    public ElytraBoost() {
        super("ElytraBooster", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isFallFlying()) {
            // Коли юзається феєрверк
            if (mc.player.isUsingItem() &&
                    mc.player.getActiveItem().getItem() == Items.FIREWORK_ROCKET &&
                    !boostPending) {
                boostPending = true;
                delayTicks = 2; // затримка, щоб гра сама дала свій буст
            }

            if (boostPending) {
                if (delayTicks > 0) {
                    delayTicks--;
                } else {
                    // Гарантований буст
                    Vec3d vel = mc.player.getVelocity();
                    Vec3d direction = vel.normalize();
                    double currentBps = vel.length();
                    double targetBps = boostValue.getValue();

                    if (currentBps < targetBps) {
                        double delta = targetBps - currentBps;
                        Vec3d added = direction.multiply(delta);
                        mc.player.addVelocity(added.x, added.y, added.z);
                    }

                    boostPending = false;
                }
            }
        } else {
            boostPending = false;
        }
    }
}