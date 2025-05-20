package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TridentBowAim extends Module {
    private final Setting<Boolean> rage = new Setting<>("Rage", false);
    private final Setting<Boolean> ignoreInvis = new Setting<>("IgnoreInvis", true);
    private final Setting<Boolean> ignoreNaked = new Setting<>("IgnoreNaked", true);
    private final Setting<Boolean> priorityNetherite = new Setting<>("PriorityNetherite", true);
    private final Setting<Float> range = new Setting<>("Range", 20f, 10f, 30f);

    public TridentBowAim() {
        super("TridentBowAim", Category.COMBAT);
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (mc.player == null || mc.world == null) return;

        if (!isHoldingBowOrTrident(mc.player)) return;

        PlayerEntity target = getTarget();
        if (target == null) return;

        faceTarget(target);
    }

    private PlayerEntity getTarget() {
        List<PlayerEntity> targets = mc.world.getPlayers().stream()
                .filter(p -> p != mc.player)
                .filter(p -> mc.player.distanceTo(p) <= range.getValue())
                .filter(p -> !ignoreInvis.getValue() || !p.isInvisible())
                .filter(p -> !ignoreNaked.getValue() || isArmored(p))
                .sorted(Comparator.comparingDouble(p -> mc.player.distanceTo(p)))
                .collect(Collectors.toList());

        if (priorityNetherite.getValue()) {
            targets = targets.stream()
                    .sorted((a, b) -> Boolean.compare(isWearingNetherite(b), isWearingNetherite(a)))
                    .collect(Collectors.toList());
        }

        return targets.isEmpty() ? null : targets.get(0);
    }

    private void faceTarget(PlayerEntity target) {
        Vec3d vec = target.getPos().add(0, target.getStandingEyeHeight(), 0);
        double dx = vec.x - mc.player.getX();
        double dy = vec.y - (mc.player.getY() + mc.player.getStandingEyeHeight());
        double dz = vec.z - mc.player.getZ();

        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)(MathHelper.atan2(dz, dx) * 57.29577951308232) - 90.0F;
        float pitch = (float)-(MathHelper.atan2(dy, dist) * 57.29577951308232);

        if (rage.getValue()) {
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        } else {
            mc.player.setYaw(MathHelper.lerp(0.2f, mc.player.getYaw(), yaw));
            mc.player.setPitch(MathHelper.lerp(0.2f, mc.player.getPitch(), pitch));
        }
    }

    private boolean isWearingNetherite(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem().toString().toLowerCase().contains("netherite") ||
                player.getEquippedStack(EquipmentSlot.CHEST).getItem().toString().toLowerCase().contains("netherite") ||
                player.getEquippedStack(EquipmentSlot.LEGS).getItem().toString().toLowerCase().contains("netherite") ||
                player.getEquippedStack(EquipmentSlot.FEET).getItem().toString().toLowerCase().contains("netherite");
    }

    private boolean isArmored(PlayerEntity player) {
        return !player.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
    }

    private boolean isHoldingBowOrTrident(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        return stack.getItem() instanceof BowItem || stack.getItem() instanceof TridentItem;
    }
}
