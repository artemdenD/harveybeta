package thunder.hack.features.modules.movement;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class JetBow extends Module {
    private final Setting<Float> power = new Setting<>("Power", 0.9f, 0.1f, 1.0f);
    private final Setting<Integer> delay = new Setting<>("Delay", 20, 5, 100);
    private final Setting<Boolean> elytraOnly = new Setting<>("ElytraOnly", true);

    private int ticksPassed = 0;

    public JetBow() {
        super("JetBow", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (elytraOnly.getValue() && !mc.player.isFallFlying()) return;

        if (!hasBow() || !mc.player.getInventory().contains(Items.ARROW.getDefaultStack())) return;

        if (ticksPassed < delay.getValue()) {
            ticksPassed++;
            return;
        }

        if (mc.player.getMainHandStack().getItem() instanceof BowItem) {
            mc.player.setPitch(-90f); // Дивиться вниз
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.setCurrentHand(Hand.MAIN_HAND);
            ticksPassed = 0;
        }
    }

    private boolean hasBow() {
        return mc.player.getMainHandStack().getItem() == Items.BOW;
    }
}