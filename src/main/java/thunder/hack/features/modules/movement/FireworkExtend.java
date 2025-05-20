package thunder.hack.features.modules.movement;

import thunder.hack.setting.Setting;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;



public class FireworkExtend extends Module {

    private final Setting<Float> velocityMultiplier = new Setting<>("MaxVelocity", 1.1f, 1.1f, 2.0f);
    private boolean boosted = false; // флаг: вже заюзали буст чи ні

    public FireworkExtend() {
        super("FireworkExtend", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isFallFlying()) {
            ItemStack stack = mc.player.getMainHandStack();

            // Якщо тримає фейерверк і ще не бустився — дати буст
            if (stack.getItem() == Items.FIREWORK_ROCKET && !boosted) {
                boosted = true;

                float mult = velocityMultiplier.getValue();

                Vec3d velocity = mc.player.getVelocity();
                mc.player.setVelocity(new Vec3d(
                        velocity.x * mult,
                        velocity.y * mult,
                        velocity.z * mult
                ));
            }

        } else {
            // Якщо не летить — скидаємо флаг
            boosted = false;
        }
    }
}
