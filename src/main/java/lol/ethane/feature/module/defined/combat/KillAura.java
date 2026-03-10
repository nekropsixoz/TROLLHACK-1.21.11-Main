package lol.ethane.feature.module.defined.combat;

import lol.ethane.event.defined.game.*;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.defined.render.Render3DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.math.MathUtil;
import net.minecraft.class_1297;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1657;
import net.minecraft.class_1669;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2868;
import net.minecraft.class_310;
import net.minecraft.class_5671;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * KillAura - Автоматически атакует ближайших врагов
 */
public class KillAura extends Module {
    
    private static final class_310 mc = class_310.method_1551();
    
    // Основные настройки
    public final ModeProperty<RotationMode> rotationMode = new ModeProperty<>("Тип наводки", RotationMode.SPOOKYTIME);
    public final NumberProperty fov = new NumberProperty("Угол обзора", 70.0, 30.0, 360.0, 1.0);
    public final NumberProperty attackRange = new NumberProperty("Дистанция", 3.0, 3.0, 6.0, 0.1);
    public final NumberProperty preRange = new NumberProperty("Доп дистанция", 1.0, 0.0, 3.0, 0.1);
    
    // Настройки критов
    public final ModeProperty<CritMode> critMode = new ModeProperty<>("Мод Критов", CritMode.CRITS_ONLY);
    
    // Отображения цели
    public final ModeProperty<TargetESP> targetESP = new ModeProperty<>("Отображения Цели", TargetESP.DIAMOND);
    
    // Прочие настройки
    public final BooleanProperty targetEspEnabled = new BooleanProperty("Таргет есп", false);
    public final BooleanProperty displayFOV = new BooleanProperty("Отображать FOV", false);
    public final BooleanProperty dontAttackWhileEating = new BooleanProperty("Не бить когда ешь", false);
    public final BooleanProperty weaponOnly = new BooleanProperty("Бить только с оружием", false);
    public final BooleanProperty disableOnDeath = new BooleanProperty("Выключить после смерти", true);
    
    // Коррекция движения
    public final ModeProperty<MovementMode> movementMode = new ModeProperty<>("Коррекция движения", MovementMode.STRONG);
    
    // Переменные состояния
    private class_1657 target;
    private int testModeTicks = 0;
    private float tick = 0.0f;
    private int attackStabilityTicks = 0;
    private float lastAttackCooldown = 0.0f;
    private boolean lastAttackWasCrit = false;
    private int sprintBlockTicks = 0;
    private float sprintBlockTime = 0.0f;
    private int count = 0;
    
    // Multipoint
    private int multipointTick = 0;
    private float multipointYaw = 0.0f;
    private float multipointPitch = 0.0f;
    private float multipointYawTarget = 0.0f;
    private float multipointPitchTarget = 0.0f;
    private float microPitchJitter = 0.0f;
    
    private final Random random = new Random();
    
    public KillAura() {
        super("KillAura", "Автоматически атакует ближайших врагов", ModuleCategory.COMBAT);
        this.addProperties(
            rotationMode, fov, attackRange, preRange, critMode, targetESP,
            targetEspEnabled, displayFOV, dontAttackWhileEating, weaponOnly, disableOnDeath,
            movementMode
        );
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }
    
    @Subscribe
    public void onUpdate(PreGameTickEvent event) {
        if (disableOnDeath.getValue() && mc.field_1724.method_6032() <= 0) {
            this.toggle();
            return;
        }
        
        updateTarget();
        
        if (target == null || mc.field_1724 == null || mc.field_1687 == null) {
            reset();
            return;
        }
        
        if (shouldSkipAttack() || shouldBreakShield()) {
            return;
        }
        
        performAttack();
    }
    
    @Subscribe
    public void onMotion(PlayerMovementTickEvent event) {
        if (target == null || mc.field_1724 == null || mc.field_1687 == null) {
            reset();
            return;
        }
        
        if (shouldSkipAttack()) {
            return;
        }
        
        updateRotation();
    }
    
    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (target == null || !displayFOV.getValue()) {
            return;
        }
        
        // TODO: Реализовать FOV отображение
    }
    
    @Subscribe
    public void onRender2D(Render2DEvent event) {
        // TODO: Реализовать 2D рендеринг цели
    }
    
    private void updateTarget() {
        double maxRange = attackRange.getValue() + preRange.getValue();
        List<class_1657> potentialTargets = new ArrayList<>();
        
        for (Object obj : mc.field_1687.method_18112()) {
            if (!(obj instanceof class_1657)) continue;
            class_1657 player = (class_1657) obj;
            
            if (player == mc.field_1724) continue;
            if (player.method_5805()) continue;
            
            double distance = MathUtil.distance(player, mc.field_1724);
            if (distance <= maxRange) {
                potentialTargets.add(player);
            }
        }
        
        // Находим ближайшую цель
        class_1657 newTarget = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (class_1657 player : potentialTargets) {
            double distance = MathUtil.distance(player, mc.field_1724);
            if (distance < closestDistance) {
                closestDistance = distance;
                newTarget = player;
            }
        }
        
        if (newTarget != target) {
            testModeTicks = 0;
        }
        
        target = newTarget;
    }
    
    private void updateRotation() {
        if (target == null) return;
        
        class_243 eyePos = mc.field_1724.method_33571();
        class_243 targetPos = getTargetPosition(target);
        
        double dx = targetPos.field_1352 - eyePos.field_1352;
        double dy = targetPos.field_1351 - eyePos.field_1351;
        double dz = targetPos.field_1350 - eyePos.field_1350;
        
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) Math.toDegrees(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        
        // Применяем различные режимы ротации
        switch (rotationMode.getValue()) {
            case SPOOKYTIME:
                applySpookytimeRotation(targetYaw, targetPitch);
                break;
            case FUNSKY:
                applyFunskyRotation(targetYaw, targetPitch);
                break;
            case SNAP:
                applySnapRotation(targetYaw, targetPitch);
                break;
            case MATRIX:
                applyMatrixRotation(targetYaw, targetPitch);
                break;
        }
    }
    
    private void applySpookytimeRotation(float targetYaw, float targetPitch) {
        testModeTicks++;
        
        if (shouldCrit()) {
            tick = 4.0f;
        }
        if (tick > 0.0f) {
            tick -= 1.0f;
        }
        
        // Multipoint логика
        if (multipointTick <= 0) {
            multipointTick = random.nextInt(3, 5);
            multipointYawTarget = random.nextFloat(-2.5f, 3.0f);
            multipointPitchTarget = random.nextFloat(-8.5f, 8.5f);
        }
        multipointTick--;
        
        multipointYaw += (multipointYawTarget - multipointYaw) * 0.1f;
        multipointPitch += (multipointPitchTarget - multipointPitch) * 0.1f;
        
        float speed = testModeTicks < 8 ? 15.0f : random.nextFloat(20.0f, 22.0f);
        
        float currentYaw = mc.field_1724.method_36454();
        float currentPitch = mc.field_1724.method_36455();
        
        float yawDelta = Math.abs(wrapDegrees(targetYaw - currentYaw));
        float pitchDelta = Math.abs(targetPitch - currentPitch);
        
        boolean isClose = yawDelta < 6.0f && pitchDelta < 6.0f;
        
        float finalYaw, finalPitch;
        
        if (isClose) {
            if (multipointTick <= 0) {
                microPitchJitter = random.nextFloat(-4.0f, 5.0f);
            }
            finalYaw = targetYaw + multipointYaw;
            finalPitch = Math.max(-90.0f, Math.min(90.0f, currentPitch + microPitchJitter));
        } else {
            finalYaw = targetYaw + multipointYaw;
            finalPitch = targetPitch + multipointPitch;
        }
        
        class_241 rotation = new class_241(finalYaw, finalPitch);
        RotationHelper.getClientHandler().setRotation(rotation);
    }
    
    private void applyFunskyRotation(float targetYaw, float targetPitch) {
        testModeTicks++;
        
        if (shouldCrit()) {
            tick = 4.0f;
        }
        if (tick > 0.0f) {
            tick -= 1.0f;
        }
        
        // Jitter эффекты
        float jitterX = (float) Math.sin(System.currentTimeMillis() / 5.0) * 1.5f;
        float jitterZ = (float) Math.sin(System.currentTimeMillis() / 5.0) * 1.5f;
        float wave = (float) Math.cos(System.currentTimeMillis() / 1000.0) * 5.9f;
        float offset = jitterX + jitterZ * 3.11f;
        float pitchOffset = 4.9f + wave;
        
        boolean isClose = isTargetClose();
        float randomYaw = random.nextFloat(-5.0f, 5.8f);
        float randomPitch = random.nextFloat(-4.0f, 5.3f);
        
        float finalYaw = targetYaw + offset + randomYaw;
        float finalPitch = Math.max(-90.0f, Math.min(90.0f, targetPitch + pitchOffset + randomPitch));
        
        class_241 rotation = new class_241(finalYaw, finalPitch);
        RotationHelper.getClientHandler().setRotation(rotation);
    }
    
    private void applySnapRotation(float targetYaw, float targetPitch) {
        class_241 rotation = new class_241(targetYaw, targetPitch);
        RotationHelper.getClientHandler().setRotation(rotation);
    }
    
    private void applyMatrixRotation(float targetYaw, float targetPitch) {
        testModeTicks++;
        
        // Предсказание движения цели
        double deltaX = target.method_23317() - target.method_23317();
        double deltaZ = target.method_23321() - target.method_23321();
        double predictedX = target.method_23317() + deltaX * 0.15;
        double predictedZ = target.method_23321() + deltaZ * 0.15;
        
        class_243 predictedPos = new class_243(predictedX, target.method_23318(), predictedZ);
        class_243 eyePos = mc.field_1724.method_33571();
        
        double dx = predictedPos.field_1352 - eyePos.field_1352;
        double dy = predictedPos.field_1351 - eyePos.field_1351;
        double dz = predictedPos.field_1350 - eyePos.field_1350;
        
        float predictedYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float predictedPitch = (float) Math.toDegrees(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        
        float jitter = (float) Math.sin(System.currentTimeMillis() / 35.0) * 3.5f;
        
        float finalYaw = predictedYaw + jitter;
        float finalPitch = Math.max(-90.0f, Math.min(90.0f, predictedPitch));
        
        class_241 rotation = new class_241(finalYaw, finalPitch);
        RotationHelper.getClientHandler().setRotation(rotation);
    }
    
    private void performAttack() {
        attackStabilityTicks++;
        
        if (!canAttack() || !isTargetClose() || MathUtil.distance(target, mc.field_1724) > getMaxRange()) {
            return;
        }
        
        boolean isSwimming = false; // Simplified for now
        boolean isSprinting = false; // Simplified for now
        
        if (isSwimming) {
            if (rotationMode.getValue() == RotationMode.SNAP) {
                class_241 rotation = getRotationToEntity(target);
                RotationHelper.getClientHandler().setRotation(rotation);
            }
            attackEntity(target);
            attackStabilityTicks = 0;
            lastAttackWasCrit = false;
            count = (count + 1) % 2;
            return;
        }
        
        boolean isNearGround = mc.field_1724.method_23318() - Math.floor(mc.field_1724.method_23318()) <= 0.05;
        boolean smartCrit = critMode.getValue() == CritMode.SMART_CRITS && isNearGround;
        
        if (isSprinting && !smartCrit) {
            mc.field_1724.method_7350();
            sprintBlockTicks = 1;
            sprintBlockTime = 1.5f;
        }
        
        if (rotationMode.getValue() == RotationMode.SNAP) {
            class_241 rotation = getRotationToEntity(target);
            RotationHelper.getClientHandler().setRotation(rotation);
        }
        
        attackEntity(target);
        attackStabilityTicks = 0;
        lastAttackWasCrit = shouldCrit();
        count = (count + 1) % 2;
    }
    
    private boolean shouldBreakShield() {
        if (target == null) return false;
        
        class_1799 heldItem = target.method_6047();
        if (heldItem != null && heldItem.method_7909() == class_1802.field_8476) { // SHIELD
            // Находим топор в инвентаре
            for (int i = 0; i < 9; i++) {
                class_1799 stack = mc.field_1724.method_31548().method_5438(i);
                if (stack != null && isAxeItem(stack.method_7909())) {
                    // Меняем на топор, атакуем, возвращаем обратно
                    int currentSlot = mc.field_1724.method_31548().method_67532();
                    mc.field_1724.field_3944.method_52787(new class_2868(i));
                    attackEntity(target);
                    mc.field_1724.field_3944.method_52787(new class_2868(currentSlot));
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isAxeItem(net.minecraft.class_1792 item) {
        return item == class_1802.field_8456 || // DIAMOND_AXE
               item == class_1802.field_8457 || // IRON_AXE
               item == class_1802.field_8458 || // GOLDEN_AXE
               item == class_1802.field_8459;   // STONE_AXE
    }
    
    private boolean shouldSkipAttack() {
        if (dontAttackWhileEating.getValue() && mc.field_1724.method_6054()) {
            return true;
        }
        
        if (weaponOnly.getValue()) {
            class_1799 heldItem = mc.field_1724.method_6047();
            if (heldItem == null || !isWeaponItem(heldItem.method_7909())) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isWeaponItem(net.minecraft.class_1792 item) {
        // Simplified weapon check - just check if it's a sword or axe
        return isAxeItem(item); // AxeItem
    }
    
    private void attackEntity(class_1657 entity) {
        mc.field_1724.method_6104(class_1268.field_5808);
    }
    
    private boolean canAttack() {
        float attackCooldown = mc.field_1724.method_7261(0.5F);
        lastAttackCooldown = Math.max(0.0f, Math.min(1.0f, attackCooldown));
        return lastAttackCooldown >= 0.93f;
    }
    
    private boolean shouldCrit() {
        if (critMode.getValue() == CritMode.CRITS_ONLY) {
            return true;
        }
        
        // Smart crits - только когда находимся на земле
        boolean isNearGround = mc.field_1724.method_23318() - Math.floor(mc.field_1724.method_23318()) <= 0.05;
        
        return critMode.getValue() == CritMode.SMART_CRITS && isNearGround;
    }
    
    private boolean isTargetClose() {
        if (target == null) return false;
        
        double distance = MathUtil.distance(target, mc.field_1724);
        return distance <= getMaxRange();
    }
    
    private double getMaxRange() {
        double baseRange = attackRange.getValue();
        // Simplified - assume normal reach
        return Math.max(baseRange, 3.0);
    }
    
    private class_243 getTargetPosition(class_1657 entity) {
        double heightRatio = MathUtil.distance(entity, mc.field_1724) / getMaxRange();
        double targetY = Math.max(entity.method_23318(), entity.method_23318() + heightRatio * 0.5);
        
        return new class_243(
            entity.method_23317(),
            targetY,
            entity.method_23321()
        );
    }
    
    private class_241 getRotationToEntity(class_1657 entity) {
        class_243 eyePos = mc.field_1724.method_33571();
        class_243 targetPos = getTargetPosition(entity);
        
        double dx = targetPos.field_1352 - eyePos.field_1352;
        double dy = targetPos.field_1351 - eyePos.field_1351;
        double dz = targetPos.field_1350 - eyePos.field_1350;
        
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, distance));
        
        return new class_241(yaw, pitch);
    }
    
    private float wrapDegrees(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }
    
    private void reset() {
        target = null;
        testModeTicks = 0;
        tick = 0.0f;
        attackStabilityTicks = 0;
        lastAttackCooldown = 0.0f;
        lastAttackWasCrit = false;
        sprintBlockTicks = 0;
        sprintBlockTime = 0.0f;
        count = 0;
        multipointTick = 0;
        multipointYaw = 0.0f;
        multipointPitch = 0.0f;
        microPitchJitter = 0.0f;
    }
    
    // Enums для настроек
    public enum RotationMode {
        SPOOKYTIME("Spookytime"),
        FUNSKY("Funsky"),
        SNAP("Снапы"),
        MATRIX("Matrix");
        
        private final String name;
        
        RotationMode(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum CritMode {
        CRITS_ONLY("Только Криты"),
        SMART_CRITS("Умные Криты");
        
        private final String name;
        
        CritMode(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum TargetESP {
        DIAMOND("Ромб"),
        IMAGE("Картинка"),
        RING("Кольцо"),
        GHOST("Призраки"),
        SPIRIT("Духи"),
        CHAINS("Цепи");
        
        private final String name;
        
        TargetESP(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum MovementMode {
        STRONG("Сильная"),
        FREE("Свободная"),
        PURSUIT("Преследования"),
        TARGET("Таргет");
        
        private final String name;
        
        MovementMode(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
