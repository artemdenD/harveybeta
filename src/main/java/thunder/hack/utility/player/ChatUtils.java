package thunder.hack.utility.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void info(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of("[BuffHelper] " + message), false);
        }
    }

    public static void plain(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of(message), false);
        }
    }
}
