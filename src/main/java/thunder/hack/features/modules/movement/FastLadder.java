package thunder.hack.features.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class FastLadder extends Module {
    private final Setting<Integer> speed = new Setting<>("Speed", 1, 0, 3);

    public FastLadder() {
        super("FastLadder", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        BlockPos pos = mc.player.getBlockPos();

        if (mc.world.getBlockState(pos).getBlock() == Blocks.LADDER ||
                mc.world.getBlockState(pos.offset(mc.player.getHorizontalFacing())).getBlock() == Blocks.LADDER) {

            if (mc.player.input.jumping) {
                double baseSpeed = switch (speed.getValue()) {
                    case 0 -> 0.2;
                    case 1 -> 0.3;
                    case 2 -> 0.5;
                    case 3 -> 0.75;
                    default -> 0.3;
                };
                mc.player.setVelocity(mc.player.getVelocity().x, baseSpeed, mc.player.getVelocity().z);
            }
        }
    }
}
