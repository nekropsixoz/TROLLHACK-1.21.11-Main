package ru.noxium.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
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
@IModule(name = "AutoClicker", description = "Автоматический кликер", category = Category.Combat, bind = -1)
public class AutoClicker extends Module {

    final BindSettings activateKey = new BindSettings("Активация", -1);
    final SliderSetting cpsSetting = new SliderSetting("CPS", 10.0f, 1.0f, 20.0f, 1.0f, false);
    final BooleanSetting holdMode = new BooleanSetting("Hold Mode", false);
    final BooleanSetting jitterMode = new BooleanSetting("Jitter Mode", false);
    final BooleanSetting breakBlocks = new BooleanSetting("Break Blocks", true);

    enum State {
        IDLE,
        CLICKING,
        COOLDOWN
    }

    State currentState = State.IDLE;
    long lastClickTime = 0;
    long clickDelay = 0;
    boolean isHolding = false;
    int clickCounter = 0;
    double randomOffset = 0;

    public AutoClicker() {
        super();
        addSettings(activateKey, cpsSetting, holdMode, jitterMode, breakBlocks);
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

        boolean matches = false;

        // KeyInputEvent doesn't have getType(), we need to check if it's a key event
        matches = e.key() == bindKey;

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

        boolean matches = false;

        // MouseButtonEvent doesn't have getType(), we check if it's a mouse button
        matches = e.button() == bindKey;

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
        if (!canClick()) {
            return;
        }

        currentState = State.CLICKING;
        lastClickTime = System.currentTimeMillis();
        calculateClickDelay();
        performClick();
    }

    private void processTick() {
        if (mc.player == null || mc.interactionManager == null) {
            reset();
            return;
        }

        long currentTime = System.currentTimeMillis();

        switch (currentState) {
            case CLICKING:
                if (holdMode.get() && !isHolding) {
                    reset();
                    return;
                }

                if (currentTime - lastClickTime >= clickDelay) {
                    if (canClick()) {
                        performClick();
                        lastClickTime = currentTime;
                        calculateClickDelay();
                        currentState = State.COOLDOWN;
                    } else {
                        reset();
                    }
                }
                break;

            case COOLDOWN:
                if (currentTime - lastClickTime >= 50) {
                    if (holdMode.get() && isHolding) {
                        currentState = State.CLICKING;
                    } else {
                        reset();
                    }
                }
                break;
        }
    }

    private void performClick() {
        if (mc.options == null) return;

        try {
            // Проверяем, можно ли кликнуть по цели
            if (mc.crosshairTarget != null && !breakBlocks.get()) {
                if (mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                    return;
                }
            }

            // Выполняем клик
            mc.options.attackKey.setPressed(true);
            mc.options.attackKey.setPressed(false);
            
            clickCounter++;

            // Добавляем джиттер если включен
            if (jitterMode.get()) {
                addJitter();
            }

        } catch (Exception ex) {
            reset();
        }
    }

    private void calculateClickDelay() {
        double cps = cpsSetting.get();
        double baseDelay = 1000.0 / cps;

        if (jitterMode.get()) {
            // Добавляем случайную вариацию ±15%
            randomOffset = (Math.random() - 0.5) * 0.3; // от -0.15 до +0.15
            baseDelay *= (1.0 + randomOffset);
        }

        clickDelay = (long) Math.max(50, baseDelay); // Минимальная задержка 50мс
    }

    private void addJitter() {
        if (mc.player == null) return;

        // Добавляем небольшое случайное движение мышью
        float jitterAmount = 0.1f;
        float randomYaw = (float) (Math.random() - 0.5) * jitterAmount;
        float randomPitch = (float) (Math.random() - 0.5) * jitterAmount;

        mc.player.setYaw(mc.player.getYaw() + randomYaw);
        mc.player.setPitch(Math.max(-90, Math.min(90, mc.player.getPitch() + randomPitch)));
    }

    private boolean canClick() {
        if (mc.player == null || mc.interactionManager == null) {
            return false;
        }

        // Проверяем, есть ли в руке предмет для клика
        ItemStack mainHand = mc.player.getMainHandStack();
        if (mainHand.isEmpty()) {
            return false;
        }

        // Проверяем, не находимся ли мы в меню
        if (mc.currentScreen != null) {
            return false;
        }

        // Проверяем, не ломаем ли мы блок (если выключено)
        if (!breakBlocks.get() && mc.crosshairTarget != null) {
            if (mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                return false;
            }
        }

        return true;
    }

    private void reset() {
        currentState = State.IDLE;
        isHolding = false;
        lastClickTime = 0;
        clickDelay = 0;
        clickCounter = 0;
        randomOffset = 0;

        if (mc.options != null) {
            mc.options.attackKey.setPressed(false);
        }
    }

    // Геттеры для информации
    public int getCurrentCPS() {
        if (clickDelay == 0) return 0;
        return (int) Math.round(1000.0 / clickDelay);
    }

    public int getTotalClicks() {
        return clickCounter;
    }

    public boolean isActive() {
        return currentState != State.IDLE;
    }
}
