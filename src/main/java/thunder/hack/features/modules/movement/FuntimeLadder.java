package thunder.hack.features.modules.movement;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class FuntimeLadder extends Module {
    private final Setting<Integer> climbSpeed = new Setting<>("ClimbSpeed", 0, 0, 2);

    public FuntimeLadder() {
        super("SpiderLadder", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (!hasLadder()) {
            disable(isRu() ? "У вас немає драбини!" : "No ladder found!");
            return;
        }

        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos ladderPos = playerPos.up();
        Direction[] directions = Direction.values();

        boolean ladderPlaced = false;

        for (Direction dir : directions) {
            if (dir.getAxis().isHorizontal()) {
                BlockPos supportPos = ladderPos.offset(dir);
                BlockState supportState = mc.world.getBlockState(supportPos);
                if (!supportState.isAir() && supportState.isSideSolidFullSquare(mc.world, supportPos, dir.getOpposite())) {
                    placeLadder(ladderPos, dir.getOpposite());
                    ladderPlaced = true;
                    break;
                }
            }
        }

        if (!ladderPlaced) return;

        // Лазіння "легітне" — просто додаємо маленьку Y-швидкість
        if (isOnLadder()) {
            double climbY = switch (climbSpeed.getValue()) {
                case 1 -> 0.2;
                case 2 -> 0.22;
                case 3 -> 0.30;
                case 4 -> 0.5;
                default -> 0.118; // нормальна швидкість лазіння
            };
            mc.player.setVelocity(mc.player.getVelocity().x, climbY, mc.player.getVelocity().z);
        }
    }

    private boolean hasLadder() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.LADDER) return true;
        }
        return false;
    }

    private void placeLadder(BlockPos pos, Direction face) {
        BlockHitResult hitResult = new BlockHitResult(
                Vec3d.ofCenter(pos),
                face,
                pos,
                false
        );
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);



    }

    private boolean isOnLadder() {
        BlockPos pos = mc.player.getBlockPos();
        return mc.world.getBlockState(pos).getBlock() == Blocks.LADDER ||
                mc.world.getBlockState(pos.up()).getBlock() == Blocks.LADDER;
    }


}
