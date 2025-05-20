package thunder.hack.features.modules.movement;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class HighJump extends Module {

    public enum JumpMode {
        Water,
        Default,
        MatrixOld,
        Intave
    }

    public final Setting<JumpMode> mode = new Setting<>("Mode", JumpMode.Default);
    public final Setting<Integer> blocks = new Setting<>("Blocks", 3, 2, 50, v -> mode.getValue() == JumpMode.Default);

    public HighJump() {
        super("HighJump", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        switch (mode.getValue()) {
            case Water -> jumpOnWater();
            case Default -> normalJump();
            case MatrixOld -> matrixOldJump();
            case Intave -> intaveElytraJump();
        }

        disable();
    }

    private void jumpOnWater() {
        mc.player.jump();
        mc.player.swingHand(Hand.MAIN_HAND);
        sendMessage(isRu() ? "Стрибаємо по воді!" : "Jumping on water!");
    }

    private void normalJump() {
        for (int i = 0; i < blocks.getValue(); i++) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.42F, mc.player.getVelocity().z);
        }
        sendMessage(isRu() ? "Звичайний стрибок!" : "Default high jump!");
    }

    private void matrixOldJump() {
        mc.player.setVelocity(mc.player.getVelocity().x, 0.8F, mc.player.getVelocity().z);
        sendMessage(isRu() ? "Matrix старий режим!" : "Matrix old mode!");
    }

    private void intaveElytraJump() {
        if (mc.player.getInventory().armor.get(2).getItem() != Items.ELYTRA) {
            sendMessage(isRu() ? "Потрібна елітра!" : "You need an Elytra!");
            return;
        }
        mc.player.jump();
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
        sendMessage(isRu() ? "Intave стрибок!" : "Intave style jump!");
    }
}
