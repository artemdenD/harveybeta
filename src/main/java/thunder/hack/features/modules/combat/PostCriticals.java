package thunder.hack.features.modules.combat;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class PostCriticals extends Module {
    private final Setting<Boolean> onBlindness = new Setting<>("BlindnessCrit", true);
    private final Setting<Boolean> removeJumpBoost = new Setting<>("RemoveJumpBoost", true);
    private final Setting<Boolean> fastAirCrit = new Setting<>("FastAirCrit", true);
    private final Setting<Boolean> groundSpoof = new Setting<>("GroundSpoof", true);
    private final Setting<Float> airCritOffset = new Setting<>("Offset", 0.1f, 0.01f, 0.2f);

    public PostCriticals() {
        super("PostCriticals", Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck() || mc.player == null || mc.world == null) return;

        if (removeJumpBoost.getValue() && mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            mc.player.removeStatusEffect(StatusEffects.JUMP_BOOST);
        }
    }

    public void doCrit() {
        if (mc.player == null || !mc.player.isOnGround()) return;

        boolean canCrit = true;

        if (onBlindness.getValue() && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) {
            canCrit = false;
        }

        if (canCrit) {
            if (fastAirCrit.getValue()) {
                double y = mc.player.getY();
                sendPosition(y + airCritOffset.getValue(), false);
                sendPosition(y, false);
                if (groundSpoof.getValue()) {
                    sendPosition(y, true); // spoof ground
                }
            } else {
                sendPosition(mc.player.getY() + 0.1, false);
                sendPosition(mc.player.getY(), false);
            }
        }
    }

    private void sendPosition(double y, boolean onGround) {
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX(), y, mc.player.getZ(), onGround
        ));
    }
}
