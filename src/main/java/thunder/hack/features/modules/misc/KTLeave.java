package thunder.hack.features.modules.misc;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class KTLeave extends Module {

    public enum Mode {
        Aresmine
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.Aresmine);
    private final Setting<Boolean> packetSpam = new Setting<>("PacketSpam", true);
    private final Setting<Integer> packetRate = new Setting<>("PacketRate", 80, 20, 100);

    private int tickCounter = 0;

    public KTLeave() {
        super("KTLeave", Category.MISC);
    }

    @Override
    public void onEnable() {
        tickCounter = 0;
        sendMessage(isRu() ? "Ліваємо...." : "KTleaving....");
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        if (mode.getValue() == Mode.Aresmine && packetSpam.getValue()) {
            for (int i = 0; i < packetRate.getValue(); i++) {
                double x = mc.player.getX() + Math.random() * 1000 - 500;
                double y = mc.player.getY() + 1e7 + Math.random() * 10000;
                double z = mc.player.getZ() + Math.random() * 1000 - 500;
                boolean onGround = true; // античіти не люблять, коли ти "на землі", але в небі

                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                        x, y, z,
                        mc.player.getYaw(), mc.player.getPitch(),
                        onGround
                ));
            }

            // Через 2 секунди вимикаємось
            if (++tickCounter >= 40) {
                disable();
            }
        }
    }
}
