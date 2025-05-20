package thunder.hack.features.modules.misc;

import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;
import thunder.hack.utility.player.ChatUtils;

public class AutoSwap extends Module {

    private final Setting<Bind> swapButton = new Setting<>("SwapButton", new Bind(GLFW.GLFW_KEY_CAPS_LOCK, false, false));
    private final Setting<Boolean> pickWithMainHand = new Setting<>("PickWithMainHand", true);

    private ItemStack firstStack = null;
    private ItemStack secondStack = null;
    private boolean swapped = false;
    private boolean wasPressed = false;

    public AutoSwap() {
        super("AutoSwap", Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        // Призначення предметів через команди (натискаючи в головній руці)
        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_Z)) {
            firstStack = (pickWithMainHand.getValue() ? mc.player.getMainHandStack() : mc.player.getOffHandStack()).copy();
            ChatUtils.info("✅ Збережено 1-й предмет: " + firstStack.getName().getString());
        }

        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_X)) {
            secondStack = (pickWithMainHand.getValue() ? mc.player.getMainHandStack() : mc.player.getOffHandStack()).copy();
            ChatUtils.info("✅ Збережено 2-й предмет: " + secondStack.getName().getString());
        }

        // Кнопка свапу
        if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), swapButton.getValue().getKey())) {


            wasPressed = false;
            return;
        }

        if (wasPressed) return;
        wasPressed = true;

        if (firstStack == null || secondStack == null) {
            ChatUtils.info("⚠️ Спочатку вибери предмети Z і X.");
            return;
        }

        ItemStack desired = swapped ? firstStack : secondStack;
        ItemStack current = mc.player.getOffHandStack();

        // Якщо в лівій руці вже потрібний предмет — нічого не робимо
        if (ItemStack.areEqual(current, desired)) {
            swapped = !swapped;
            return;
        }

        int slot = findMatchingItem(desired);
        if (slot == -1) {
            ChatUtils.info("❌ Немає потрібного предмета в інвентарі!");
            return;
        }

        moveToOffhand(slot);
        swapped = !swapped;
    }

    private int findMatchingItem(ItemStack target) {
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (ItemStack.areEqual(stack, target)) {
                return i;
            }
        }
        return -1;
    }

    private void moveToOffhand(int invSlot) {
        if (invSlot < 0 || invSlot > 35) return;

        // Слот 45 — це ліворукий слот
        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                45, // Offhand
                invSlot,
                SlotActionType.SWAP,
                mc.player
        );
    }
}
