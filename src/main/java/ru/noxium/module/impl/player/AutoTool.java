package ru.noxium.module.impl.player;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import ru.noxium.event.EventInit;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.impl.BooleanSetting;

@IModule(name = "AutoTool", category = Category.Player, description = "Автоматически выбирает эффективный инструмент при ломании блока", bind = -1)
public class AutoTool extends Module {

    private final BooleanSetting silent = new BooleanSetting("Silent", true);

    public AutoTool() {
        addSettings(silent);
    }

    @EventInit
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null)
            return;
        if (mc.currentScreen != null)
            return;

        // Check if LMB is held down
        if (mc.options.attackKey.isPressed()) {
            if (mc.crosshairTarget instanceof BlockHitResult hitResult && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = mc.world.getBlockState(pos);

                if (state.isAir())
                    return;

                int bestSlot = getBestTool(state);
                int currentSlot = mc.player.getInventory().getSelectedSlot();

                if (bestSlot != -1 && bestSlot != currentSlot) {
                    if (silent.get()) {
                        // Silent swap: Tell server we have the tool
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bestSlot));

                        // Mine (Server sees tool)
                        mc.interactionManager.updateBlockBreakingProgress(pos, hitResult.getSide());

                        // Swap back server side
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));

                        mc.player.swingHand(Hand.MAIN_HAND);
                    } else {
                        // Regular swap
                        mc.player.getInventory().setSelectedSlot(bestSlot);
                        mc.interactionManager.updateBlockBreakingProgress(pos, hitResult.getSide());
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                } else {
                    mc.interactionManager.updateBlockBreakingProgress(pos, hitResult.getSide());
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    private int getBestTool(BlockState state) {
        int bestSlot = -1;
        float maxSpeed = 1.0f;

        int currentSlot = mc.player.getInventory().getSelectedSlot();
        ItemStack currentStack = mc.player.getInventory().getStack(currentSlot);
        if (!currentStack.isEmpty()) {
            float speed = currentStack.getMiningSpeedMultiplier(state);
            if (speed > maxSpeed) {
                maxSpeed = speed;
                bestSlot = currentSlot;
            }
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty())
                continue;
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > maxSpeed) {
                maxSpeed = speed;
                bestSlot = i;
            }
        }
        return bestSlot;
    }
}
