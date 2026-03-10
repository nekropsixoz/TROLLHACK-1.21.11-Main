package lol.ethane.utils.rotation;

import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_2338;
import java.util.Random;

/**
 * Helper class for smooth, human-like rotations.
 * Designed to bypass anticheat detection with realistic movement patterns.
 */
public class LegitRotationHelper {
    
    private static final class_310 mc = class_310.method_1551();
    private static final Random random = new Random();
    
    private float currentYaw;
    private float currentPitch;
    private long lastUpdateTime;
    private float targetYaw;
    private float targetPitch;
    
    // Rotation speed settings (degrees per tick)
    private static final float MIN_ROTATION_SPEED = 2.0f;
    private static final float MAX_ROTATION_SPEED = 8.0f;
    private static final float JITTER_AMOUNT = 1.5f;
    
    public LegitRotationHelper() {
        reset();
    }
    
    /**
     * Updates rotations with smooth interpolation and random jitter.
     */
    public void updateRotations(float desiredYaw, float desiredPitch) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        
        if (mc.field_1724 == null) return;
        
        // Add random jitter to make rotations look human
        float jitterYaw = random.nextFloat(-JITTER_AMOUNT, JITTER_AMOUNT);
        float jitterPitch = random.nextFloat(-2.0f, 2.0f);
        
        targetYaw = desiredYaw + jitterYaw;
        targetPitch = clampPitch(desiredPitch + jitterPitch);
        
        // Calculate rotation speed based on distance (human-like acceleration)
        float yawDistance = getAngleDistance(currentYaw, targetYaw);
        float pitchDistance = Math.abs(currentPitch - targetPitch);
        float totalDistance = Math.max(yawDistance, pitchDistance);
        
        float rotationSpeed = calculateRotationSpeed(totalDistance);
        
        // Smooth interpolation
        currentYaw = interpolateAngle(currentYaw, targetYaw, rotationSpeed * deltaTime);
        currentPitch = interpolateValue(currentPitch, targetPitch, rotationSpeed * deltaTime);
    }
    
    /**
     * Gets the current yaw with human-like variation.
     */
    public float getCurrentYaw() {
        return currentYaw;
    }
    
    /**
     * Gets the current pitch within human-like range.
     */
    public float getCurrentPitch() {
        return currentPitch;
    }
    
    /**
     * Checks if rotations are close enough to target.
     */
    public boolean isRotationsDone() {
        float yawDistance = getAngleDistance(currentYaw, targetYaw);
        float pitchDistance = Math.abs(currentPitch - targetPitch);
        return yawDistance < 1.0f && pitchDistance < 1.0f;
    }
    
    /**
     * Resets rotation state.
     */
    public void reset() {
        if (mc.field_1724 != null) {
            currentYaw = mc.field_1724.method_36454();
            currentPitch = mc.field_1724.method_36455();
        } else {
            currentYaw = 0.0f;
            currentPitch = 0.0f;
        }
        targetYaw = currentYaw;
        targetPitch = currentPitch;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Calculates human-like rotation speed based on distance.
     */
    private float calculateRotationSpeed(float distance) {
        // Humans rotate faster for larger distances, slower for precise adjustments
        float speed = MIN_ROTATION_SPEED + (distance / 90.0f) * (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED);
        return Math.min(speed, MAX_ROTATION_SPEED);
    }
    
    /**
     * Smooth angle interpolation with wrap-around support.
     */
    private float interpolateAngle(float current, float target, float speed) {
        float diff = getAngleDistance(current, target);
        if (Math.abs(diff) <= speed) {
            return target;
        }
        return current + Math.copySign(speed, diff);
    }
    
    /**
     * Linear value interpolation.
     */
    private float interpolateValue(float current, float target, float speed) {
        float diff = target - current;
        if (Math.abs(diff) <= speed) {
            return target;
        }
        return current + Math.copySign(speed, diff);
    }
    
    /**
     * Gets the shortest angular distance between two angles.
     */
    private float getAngleDistance(float from, float to) {
        float diff = (float) class_3532.method_15338(to - from);
        if (diff > 180.0f) {
            diff -= 360.0f;
        } else if (diff < -180.0f) {
            diff += 360.0f;
        }
        return diff;
    }
    
    /**
     * Clamps pitch to human-like range (78-82 degrees for scaffold).
     */
    private float clampPitch(float pitch) {
        // Human players typically look down at 78-82 degrees when bridging
        float minPitch = 78.0f;
        float maxPitch = 82.0f;
        return Math.max(minPitch, Math.min(maxPitch, pitch));
    }
    
    /**
     * Generates legit-looking rotation for block placement.
     */
    public static float[] generateLegitBlockRotation(float playerYaw, class_2338 blockPos) {
        if (mc.field_1724 == null) {
            return new float[]{playerYaw, 80.0f};
        }
        
        // Calculate direction to block
        class_2338 playerPos = mc.field_1724.method_24515();
        double deltaX = blockPos.method_10263() + 0.5 - playerPos.method_10263();
        double deltaZ = blockPos.method_10260() + 0.5 - playerPos.method_10260();
        double deltaY = blockPos.method_10264() + 0.5 - (playerPos.method_10264() + 1.62); // Standard eye height
        
        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));
        
        // Add human-like variation
        yaw += random.nextFloat(-1.5f, 1.5f);
        pitch = 80.0f + random.nextFloat(-2.0f, 2.0f);
        
        // Clamp to legit ranges
        pitch = Math.max(78.0f, Math.min(82.0f, pitch));
        
        return new float[]{yaw, pitch};
    }
}
