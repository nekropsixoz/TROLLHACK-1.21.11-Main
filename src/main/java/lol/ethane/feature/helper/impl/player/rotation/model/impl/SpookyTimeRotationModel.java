package lol.ethane.feature.helper.impl.player.rotation.model.impl;

import lol.ethane.feature.helper.impl.player.rotation.model.IRotationModel;
import net.minecraft.class_241;
import net.minecraft.class_3532;
import net.minecraft.class_310;
import java.util.Random;

public class SpookyTimeRotationModel implements IRotationModel {
    private static final float RETURN_SPEED = 35.0F;
    private static final float MAX_YAW_SPEED = 55.03F;
    private static final float MIN_YAW_SPEED = 42.2F;
    private static final float MAX_PITCH_SPEED = 32.2F;
    private static final float MIN_PITCH_SPEED = 9.2F;
    private static final float RANDOM_SPEED_FACTOR = 0.3F;
    private static final float YAW_RANDOM_JITTER = 3.0F;
    private static final float PITCH_RANDOM_JITTER = 0.0F;
    private static final float YAW_PITCH_COUPLING = 1.0F;
    private static final float COOLDOWN_SLOWDOWN = 1.0F;

    private final Random random = new Random();
    private float lastYawJitter;
    private float lastPitchJitter;

    @Override
    public class_241 tick(class_241 from, class_241 to, float delta) {
        class_310 mc = class_310.method_1551();
        
        float yawDelta = class_3532.method_15393(to.field_1343 - from.field_1343);
        float pitchDelta = to.field_1342 - from.field_1342;

        float yawAbs = Math.abs(yawDelta);
        float pitchAbs = Math.abs(pitchDelta);

        float yawFraction = class_3532.method_15363(yawAbs / 180.0F, 0.0F, 1.0F);
        float pitchFraction = class_3532.method_15363(pitchAbs / 90.0F, 0.0F, 1.0F);

        float yawSpeed = class_3532.method_16439(yawFraction, MIN_YAW_SPEED, MAX_YAW_SPEED);
        float pitchSpeed = class_3532.method_16439(pitchFraction, MIN_PITCH_SPEED, MAX_PITCH_SPEED);

        if (mc.field_1724 != null) {
            float cooldown = 1.0F - class_3532.method_15363(mc.field_1724.method_7261(1.0F), 0.0F, 1.0F);
            float slowdown = class_3532.method_16439(cooldown, 1.0F, COOLDOWN_SLOWDOWN);
            yawSpeed *= slowdown;
            pitchSpeed *= slowdown;
        }

        float randomScaleYaw = 1.0F + ((random.nextFloat() * 2.0F - 1.0F) * RANDOM_SPEED_FACTOR);
        float randomScalePitch = 1.0F + ((random.nextFloat() * 2.0F - 1.0F) * RANDOM_SPEED_FACTOR * YAW_PITCH_COUPLING);

        yawSpeed = class_3532.method_15363(yawSpeed * randomScaleYaw, MIN_YAW_SPEED, MAX_YAW_SPEED);
        pitchSpeed = class_3532.method_15363(pitchSpeed * randomScalePitch, MIN_PITCH_SPEED, MAX_PITCH_SPEED);

        float yawStep = class_3532.method_15363(yawDelta, -yawSpeed, yawSpeed);
        float pitchStep = class_3532.method_15363(pitchDelta, -pitchSpeed, pitchSpeed);

        lastYawJitter = randomJitter(YAW_RANDOM_JITTER, lastYawJitter);
        lastPitchJitter = randomJitter(PITCH_RANDOM_JITTER, lastPitchJitter);

        return new class_241(
            from.field_1343 + yawStep + lastYawJitter,
            class_3532.method_15363(from.field_1342 + pitchStep + lastPitchJitter, -89.0F, 90.0F)
        );
    }

    private float randomJitter(float bound, float previous) {
        if (bound <= 0.0F) return 0.0F;
        float next = (random.nextFloat() * 2.0F - 1.0F) * bound;
        return class_3532.method_16439(0.35F, previous, next);
    }
}
