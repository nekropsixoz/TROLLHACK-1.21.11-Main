package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventPacket;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.util.player.MoveUtil;
import ru.noxium.Noxium;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@IModule(name = "GuiMove", description = "Позволяет двигаться в инвентаре", category = Category.Movement, bind = -1)
@Environment(EnvType.CLIENT)
public class GuiMove extends Module {

    public final BooleanSetting noMoveInChest = new BooleanSetting("Не двигаться в сундуках", false);

    private int pauseTicks = 0;
    private Screen lastScreen = null;
    private final List<ClickSlotC2SPacket> pendingPackets = new CopyOnWriteArrayList<>();
    private boolean sendingPackets = false;

    public GuiMove() {
        addSettings(noMoveInChest);
    }

    @EventInit
    public void onUpdate(ClientTickEvent event) {
        if (mc.player == null || mc.world == null)
            return;

        if (lastScreen instanceof InventoryScreen && !(mc.currentScreen instanceof InventoryScreen)) {
            onInventoryClosed();
        }
        lastScreen = mc.currentScreen;

        final KeyBinding[] moveKeys = {
                mc.options.forwardKey,
                mc.options.backKey,
                mc.options.leftKey,
                mc.options.rightKey,
                mc.options.jumpKey,
                mc.options.sprintKey
        };

        if (pauseTicks > 0) {
            for (KeyBinding keyBinding : moveKeys) {
                keyBinding.setPressed(false);
            }
            pauseTicks--;
            return;
        }

        // Always update key states based on physical keyboard if not in restricted
        // screens
        if (mc.currentScreen != null) {
            if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof AbstractSignEditScreen) {
                return;
            }

            if (noMoveInChest.get() && mc.currentScreen instanceof GenericContainerScreen) {
                return;
            }

            for (KeyBinding keyBinding : moveKeys) {
                boolean isKeyPressed = InputUtil.isKeyPressed(mc.getWindow(), keyBinding.getDefaultKey().getCode());
                keyBinding.setPressed(isKeyPressed);
            }
        }
    }

    @EventInit
    public void onPacket(EventPacket event) {
        if (mc.player == null)
            return;
        if (!event.isSend())
            return;
        if (sendingPackets)
            return;

        Packet<?> packet = event.getPacket();

        if (packet instanceof ClickSlotC2SPacket clickPacket) {
            if (MoveUtil.isMoving() && mc.currentScreen instanceof InventoryScreen) {
                pendingPackets.add(clickPacket);
                event.cancel();
            }
        }

        if (packet instanceof CloseHandledScreenC2SPacket) {
            if (!pendingPackets.isEmpty() && MoveUtil.isMoving()) {
                event.cancel();
                sendPendingPackets();
            }
        }
    }

    private void sendPendingPackets() {
        if (pendingPackets.isEmpty())
            return;

        List<ClickSlotC2SPacket> packetsToSend = new ArrayList<>(pendingPackets);
        pendingPackets.clear();

        new Thread(() -> {
            pauseTicks = 5;

            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            sendingPackets = true;

            for (ClickSlotC2SPacket pkt : packetsToSend) {
                if (mc.player == null || mc.getNetworkHandler() == null)
                    break;

                mc.getNetworkHandler().sendPacket(pkt);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            sendingPackets = false;

            if (mc.player != null && mc.getNetworkHandler() != null) {
                mc.getNetworkHandler()
                        .sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
            }

        }, "GuiMove-PacketSender").start();
    }

    private void onInventoryClosed() {
        if (!pendingPackets.isEmpty()) {
            sendPendingPackets();
        }
    }

    public void stopMovementTemporarily(float timeInSeconds) {
        if (!this.enable)
            return;
        int ticks = Math.max(1, (int) Math.ceil(timeInSeconds * 20.0f));
        pauseTicks = Math.max(pauseTicks, ticks);
    }

    public static void stopMovement(float timeInSeconds) {
        GuiMove gm = Noxium.get.manager.get(GuiMove.class);
        if (gm != null) {
            gm.stopMovementTemporarily(timeInSeconds);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        pauseTicks = 3;
        lastScreen = mc.currentScreen;
        pendingPackets.clear();
        sendingPackets = false;
    }

    @Override
    public void onDisable() {
        pauseTicks = 0;
        lastScreen = null;
        pendingPackets.clear();
        sendingPackets = false;
        super.onDisable();
    }
}
