package ru.noxium.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.noxium.event.EventInit;
import ru.noxium.event.EventType;
import ru.noxium.event.input.KeyInputEvent;
import ru.noxium.event.input.MouseButtonEvent;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@IModule(name = "AutoTNT", description = "Автоматическая установка и подрыв вагонетки с динамитом", category = Category.Combat, bind = -1)
public class AutoTNT extends Module {

    final BindSettings activateKey = new BindSettings("Активация", -1);
    final SliderSetting delaySetting = new SliderSetting("Задержка", 5.0f, 1.0f, 20.0f, 1.0f, false);
    final BooleanSetting silentRotation = new BooleanSetting("Сайлент ротация", false);
    final BooleanSetting autoSwitch = new BooleanSetting("Авто смена", true);

    enum State {
        IDLE,
        PLACING_RAIL,
        PLACING_CART,
        DRAWING_BOW,
        SHOOTING,
        DONE
    }

    State currentState = State.IDLE;
    int actionTimer = 0;
    int originalSlot = 0;
    int railSlot = -1;
    int bowSlot = -1;
    int tntCartSlot = -1;
    BlockHitResult targetHit = null;
    boolean bowStarted = false;
    float originalPitch = 0;
    float originalYaw = 0;

    public AutoTNT() {
        super();
        addSettings(activateKey, delaySetting, silentRotation, autoSwitch);
    }

    @Override
    public void onDisable() {
        reset();
    }

    @EventInit(value = EventType.PRE)
    public void onKey(KeyInputEvent e) {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;
        if (currentState != State.IDLE) return;
        if (e.action() != 1) return;

        int bindKey = activateKey.key;

        if (bindKey == GLFW.GLFW_KEY_UNKNOWN || bindKey == -1) return;

        boolean matches = e.key() == bindKey;

        if (matches) {
            execute();
        }
    }

    @EventInit(value = EventType.PRE)
    public void onMouse(MouseButtonEvent e) {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;
        if (currentState != State.IDLE) return;
        if (e.action() != 1) return;

        int bindKey = activateKey.key;

        if (bindKey == GLFW.GLFW_KEY_UNKNOWN || bindKey == -1) return;

        boolean matches = e.button() == bindKey;

        if (matches) {
            execute();
        }
    }

    @EventInit(value = EventType.PRE)
    public void onTick(ClientTickEvent e) {
        if (mc.player == null || mc.world == null) return;

        if (currentState != State.IDLE) {
            processTick();
        }
    }

    private void execute() {
        PlayerInventory inventory = mc.player.getInventory();

        tntCartSlot = findTNTMinecart(inventory);
        railSlot = findRail(inventory);
        bowSlot = findBow(inventory);

        if (tntCartSlot == -1 || railSlot == -1 || bowSlot == -1) {
            return;
        }

        HitResult hit = mc.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        targetHit = (BlockHitResult) hit;
        originalSlot = inventory.getSelectedSlot();
        originalPitch = mc.player.getPitch();
        originalYaw = mc.player.getYaw();
        actionTimer = 0;
        bowStarted = false;

        currentState = State.PLACING_RAIL;
    }

    private void processTick() {
        if (mc.player == null || mc.interactionManager == null || mc.world == null) {
            reset();
            return;
        }

        actionTimer++;

        switch (currentState) {
            case PLACING_RAIL:
                if (actionTimer == 1) {
                    if (autoSwitch.get()) {
                        mc.player.getInventory().setSelectedSlot(railSlot);
                    }
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, targetHit);
                    currentState = State.PLACING_CART;
                    actionTimer = 0;
                }
                break;

            case PLACING_CART:
                if (actionTimer >= (int) delaySetting.get()) {
                    if (autoSwitch.get()) {
                        mc.player.getInventory().setSelectedSlot(tntCartSlot);
                    }
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, targetHit);
                    currentState = State.DRAWING_BOW;
                    actionTimer = 0;
                }
                break;

            case DRAWING_BOW:
                if (actionTimer == 1) {
                    if (autoSwitch.get()) {
                        mc.player.getInventory().setSelectedSlot(bowSlot);
                    }
                    mc.options.useKey.setPressed(true);
                    bowStarted = true;
                    currentState = State.SHOOTING;
                    actionTimer = 0;
                }
                break;

            case SHOOTING:
                if (actionTimer >= 10) { // Даем время для натяжения лука
                    if (!silentRotation.get()) {
                        // Целимся немного ниже для попадания в вагонетку
                        mc.player.setPitch(mc.player.getPitch() + 10.0f);
                    }
                } else if (actionTimer >= 12) {
                    if (bowStarted) {
                        mc.options.useKey.setPressed(false);
                        bowStarted = false;
                    }
                } else if (actionTimer >= 15) {
                    if (!silentRotation.get()) {
                        mc.player.setPitch(originalPitch);
                        mc.player.setYaw(originalYaw);
                    }
                    currentState = State.DONE;
                    actionTimer = 0;
                }
                break;

            case DONE:
                if (actionTimer >= 5) {
                    if (autoSwitch.get()) {
                        mc.player.getInventory().setSelectedSlot(originalSlot);
                    }
                    reset();
                }
                break;
        }
    }

    private void reset() {
        if (bowStarted && mc.options != null) {
            mc.options.useKey.setPressed(false);
        }
        currentState = State.IDLE;
        actionTimer = 0;
        bowStarted = false;
        targetHit = null;
        originalPitch = 0;
        originalYaw = 0;
    }

    private int findTNTMinecart(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.TNT_MINECART) {
                return i;
            }
        }
        return -1;
    }

    private int findRail(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.RAIL || 
                stack.getItem() == Items.POWERED_RAIL ||
                stack.getItem() == Items.DETECTOR_RAIL ||
                stack.getItem() == Items.ACTIVATOR_RAIL) {
                return i;
            }
        }
        return -1;
    }

    private int findBow(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.BOW) {
                return i;
            }
        }
        return -1;
    }

    public boolean isActive() {
        return currentState != State.IDLE;
    }

    public String getCurrentState() {
        return currentState.name();
    }
}
