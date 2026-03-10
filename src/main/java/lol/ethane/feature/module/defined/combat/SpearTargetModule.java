package lol.ethane.feature.module.defined.combat;

import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.math.MathUtil;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2868;
import net.minecraft.class_310;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpearTargetModule extends Module {

    private static final class_310 mc = class_310.method_1551();

    public final NumberProperty range        = new NumberProperty("Range", 6.0, 1.0, 20.0, 0.5);
    public final NumberProperty smoothing    = new NumberProperty("Smoothing", 6.0, 1.0, 20.0, 0.5);
    public final BooleanProperty autoRocket  = new BooleanProperty("Auto Firework", true);

    public SpearTargetModule() {
        super("Spear Target", "Aim at target when holding a trident.", ModuleCategory.COMBAT);
        this.addProperties(range, smoothing, autoRocket);
    }

    @Subscribe
    public void onTick(PreGameTickEvent event) {
        if (mc.field_1724 == null || mc.field_1687 == null) return;
        if (!isHoldingTrident()) return;

        class_1657 target = getTarget();
        if (target == null) return;

        class_243 eyePos = mc.field_1724.method_33571();
        class_238 box    = target.method_5829();
        class_243 aimPos = MathUtil.getBestAimPoint(box);

        // Calculate target rotation
        class_241 targetRotation = getRotations(eyePos, aimPos);

        // Smooth rotation — interpolate instead of snap
        class_241 smoothed = smoothRotation(targetRotation);

        // SILENT aim — only sends to server, camera does NOT move
        RotationHelper.getClientHandler().setRotation(smoothed);

        // Auto firework: use firework if trident is in air (riptide)
        if ((boolean) autoRocket.getValue()) {
            tryUseFirework();
        }
    }

    /**
     * Smoothly interpolate current rotation towards target rotation.
     * Prevents the snap/jerk that causes the trident to fly the wrong way.
     */
    private class_241 smoothRotation(class_241 target) {
        float currentYaw   = mc.field_1724.method_36454();
        float currentPitch = mc.field_1724.method_36455();

        float speed = (float) (1.0 / (double) smoothing.getValue());

        float yaw   = interpolateAngle(currentYaw,   target.field_1343, speed);
        float pitch = interpolateAngle(currentPitch, target.field_1342, speed);

        return new class_241(yaw, pitch);
    }

    /**
     * Interpolates between two angles correctly handling the -180/180 wrap.
     */
    private float interpolateAngle(float current, float target, float speed) {
        float diff = target - current;

        // Normalize to [-180, 180]
        while (diff > 180.0f)  diff -= 360.0f;
        while (diff < -180.0f) diff += 360.0f;

        return current + diff * speed;
    }

    /**
     * Tries to use a firework rocket from inventory (for riptide trident boost).
     */
    private void tryUseFirework() {
        if (mc.field_1724 == null) return;

        // Check if player is in water/rain (riptide condition)
        // Simplified check - just use rain check for now
        boolean canRiptide = mc.field_1724.method_5799();
        if (!canRiptide) return;

        // Find firework in hotbar
        for (int i = 0; i < 9; i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (isFireworkItem(stack)) {
                // Simple slot change
                int prevSlot = mc.field_1724.method_31548().method_67532();
                mc.field_1724.field_3944.method_52787(new class_2868(i));
                mc.field_1724.field_3944.method_52787(new class_2868(prevSlot));
                break;
            }
        }
    }

    private boolean isFireworkItem(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        net.minecraft.class_1792 item = stack.method_7909();
        return item != null && item.toString().toLowerCase().contains("firework");
    }

    private boolean isHoldingTrident() {
        class_1799 main = mc.field_1724.method_6047();
        class_1799 off  = mc.field_1724.method_6079();
        return isSpearItem(main) || isSpearItem(off);
    }

    private boolean isSpearItem(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        net.minecraft.class_1792 item = stack.method_7909();
        return item == class_1802.field_63384
            || item == class_1802.field_63385
            || item == class_1802.field_63386
            || item == class_1802.field_63387
            || item == class_1802.field_63388
            || item == class_1802.field_63389
            || item == class_1802.field_63390;
    }

    private class_1657 getTarget() {
        double maxRange = (Double) range.getValue();
        List<class_1657> targets = new ArrayList<>();

        for (Object obj : mc.field_1687.method_18112()) {
            if (!(obj instanceof class_1297)) continue;
            class_1297 entity = (class_1297) obj;

            if (!(entity instanceof class_1657)) continue;
            class_1657 player = (class_1657) entity;

            if (player == mc.field_1724) continue;
            if (player.method_5805()) continue;

            double dist = MathUtil.distance(entity, mc.field_1724);
            if (dist <= maxRange) targets.add(player);
        }

        if (targets.isEmpty()) return null;

        targets.sort(Comparator.comparingDouble(
            p -> MathUtil.distance(p, mc.field_1724)
        ));

        return targets.get(0);
    }

    private class_241 getRotations(class_243 from, class_243 to) {
        double dx = to.field_1352 - from.field_1352;
        double dy = to.field_1351 - from.field_1351;
        double dz = to.field_1350 - from.field_1350;

        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw   = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        return new class_241(yaw, pitch);
    }
}