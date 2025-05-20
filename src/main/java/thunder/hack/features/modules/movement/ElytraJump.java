package thunder.hack.features.modules.movement;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class ElytraJump extends Module {

    private final Setting<Integer> jumps = new Setting<>("MultiJump", 1, 1, 20);
    private final Setting<Float> jumpFactor = new Setting<>("JumpFactor", 0.42f, 0.2f, 20.0f);

    private int jumpsUsed = 0;

    public ElytraJump() {
        super("ElytraJump", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        jumpsUsed = 0;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isFallFlying()) {
            if (mc.options.jumpKey.isPressed()) {
                if (jumpsUsed < jumps.getValue()) {
                    mc.player.setVelocity(mc.player.getVelocity().x, jumpFactor.getValue(), mc.player.getVelocity().z);
                    jumpsUsed++;
                }
            } else {
                // Коли не натискаєш — скидає готовність до нового стрибка
                jumpsUsed = 0;
            }
        } else {
            jumpsUsed = 0; // якщо ти не літаєш — ресет
        }
    }
}