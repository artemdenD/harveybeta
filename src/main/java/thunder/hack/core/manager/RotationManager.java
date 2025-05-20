package thunder.hack.core.manager;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RotationManager {

    public float getYawTo(Entity target) {
        double dx = target.getX() - net.minecraft.client.MinecraftClient.getInstance().player.getX();
        double dz = target.getZ() - net.minecraft.client.MinecraftClient.getInstance().player.getZ();
        return (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
    }

    public float getPitchTo(Entity target) {
        double dx = target.getX() - net.minecraft.client.MinecraftClient.getInstance().player.getX();
        double dy = target.getY() + target.getStandingEyeHeight() - net.minecraft.client.MinecraftClient.getInstance().player.getEyeY();
        double dz = target.getZ() - net.minecraft.client.MinecraftClient.getInstance().player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        return (float) -(MathHelper.atan2(dy, dist) * (180D / Math.PI));
    }
}
