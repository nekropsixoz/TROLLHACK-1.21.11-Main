package ru.noxium.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class DropAllManager {
    private static final DropAllManager INSTANCE = new DropAllManager();
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean isDropping = false;
    private int currentSlot = 0;
    private float timer = 0;
    private static final float DROP_DELAY = 1.5f; // Total time in seconds
    private int totalSlots = 0;
    private float delayPerSlot = 0;

    private DropAllManager() {
    }

    public static DropAllManager getInstance() {
        return INSTANCE;
    }

    public void startDropping() {
        if (mc.player == null || isDropping)
            return;

        // Count non-empty slots
        totalSlots = countNonEmptySlots();

        if (totalSlots == 0) {
            return;
        }

        // Calculate delay per slot to spread drops over 1.5 seconds
        delayPerSlot = DROP_DELAY / totalSlots;

        isDropping = true;
        currentSlot = 0;
        timer = 0;
    }

    private int countNonEmptySlots() {
        if (mc.player == null)
            return 0;

        int count = 0;
        PlayerInventory inventory = mc.player.getInventory();

        // Count slots 0-35 (main inventory + hotbar, excluding armor and offhand)
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                count++;
            }
        }

        return count;
    }

    public void tick() {
        if (!isDropping || mc.player == null || mc.interactionManager == null) {
            return;
        }

        // Update timer
        timer += 1.0f / 60.0f;

        // Calculate how many items should have been dropped by now
        int targetDropped = (int) (timer / delayPerSlot);

        // Drop items until we catch up
        while (currentSlot < 36 && getDroppedCount() < targetDropped) {
            ItemStack stack = mc.player.getInventory().getStack(currentSlot);

            if (!stack.isEmpty()) {
                int screenSlot = currentSlot < 9 ? currentSlot + 36 : currentSlot;

                try {
                    mc.interactionManager.clickSlot(
                            mc.player.currentScreenHandler.syncId,
                            screenSlot,
                            1, // Right click to drop
                            SlotActionType.THROW,
                            mc.player);
                } catch (Exception e) {
                }
            }

            currentSlot++;
        }

        // Check if we're done
        if (currentSlot >= 36 || timer >= DROP_DELAY) {
            isDropping = false;
            currentSlot = 0;
            timer = 0;
        }
    }

    private int getDroppedCount() {
        int count = 0;
        for (int i = 0; i < currentSlot && i < 36; i++) {
            count++;
        }
        return count;
    }

    public boolean isDropping() {
        return isDropping;
    }

    public void stop() {
        isDropping = false;
        currentSlot = 0;
        timer = 0;
    }
}
