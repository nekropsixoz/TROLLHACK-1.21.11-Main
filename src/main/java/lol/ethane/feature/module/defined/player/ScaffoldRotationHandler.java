package lol.ethane.feature.module.defined.player;

import lol.ethane.utils.math.MovementUtil;
import lol.ethane.utils.simulation.DirectionalInput;
import net.minecraft.class_310;
import net.minecraft.class_3532;

/**
 * Handles rotation calculations for scaffold module.
 * Separated from main module for better code organization.
 */
public class ScaffoldRotationHandler {
    
    private static final class_310 mc = class_310.method_1551();
    
    private float currentYaw = -180.0F;
    private float currentPitch = 0.0F;
    private boolean godBridgeRightSide = false;
    
    public void updateRotations(ScaffoldModule.RotationMode mode) {
        if (mc.field_1724 == null) {
            return;
        }

        float baseYaw = mc.field_1724.method_36454();
        float basePitch = 80.0F;

        float targetYaw = baseYaw;
        float targetPitch = basePitch;

        switch (mode) {
            case GODBRIDGE:
                float[] godBridgeRotations = computeGodBridgeRotations(baseYaw);
                targetYaw = godBridgeRotations[0];
                targetPitch = godBridgeRotations[1];
                break;
            case BACKWARDS:
                targetYaw += 180.0F;
                break;
            case SIDEWAYS:
                targetYaw += 90.0F;
                break;
            case DEFAULT:
                targetYaw += 180.0F;
                break;
            case NONE:
                targetYaw = mc.field_1724.method_36454();
                targetPitch = mc.field_1724.method_36455();
                break;
            case SMOOTH:
                targetYaw += 180.0F;
                currentYaw = smoothAngle(currentYaw, targetYaw, 15.0F);
                currentPitch = smoothAngle(currentPitch, targetPitch, 15.0F);
                return;
            default:
                break;
        }

        currentYaw = targetYaw;
        currentPitch = targetPitch;
    }
    
    public float getCurrentYaw() {
        return currentYaw;
    }
    
    public float getCurrentPitch() {
        return currentPitch;
    }
    
    /**
     * GodBridge rotations inspired by LiquidBounce ScaffoldGodBridgeTechnique.
     */
    private float[] computeGodBridgeRotations(float baseYaw) {
        var movementInput = mc.field_1724.field_3913.method_3128();
        DirectionalInput input = DirectionalInput.fromMovement(movementInput.field_1343, movementInput.field_1342);

        boolean hasInput = input.forwards() || input.backwards() || input.left() || input.right();
        if (!hasInput) {
            return new float[]{baseYaw + 180.0F, 75.0F};
        }

        float direction = MovementUtil.getMovementDirectionOfInput(baseYaw, input) + 180.0F;
        float movingYaw = Math.round(direction / 45.0F) * 45.0F;
        boolean isMovingStraight = Math.abs(movingYaw % 90.0F) < 0.1F;

        float finalYaw;
        float finalPitch;

        if (isMovingStraight) {
            if (mc.field_1724.method_24828()) {
                godBridgeRightSide = !godBridgeRightSide;
            }
            finalYaw = movingYaw + (godBridgeRightSide ? 45.0F : -45.0F);
            finalPitch = 75.7F;
        } else {
            finalYaw = movingYaw;
            finalPitch = 75.6F;
        }

        finalYaw = (float) class_3532.method_15338(finalYaw);
        return new float[]{finalYaw, finalPitch};
    }

    /**
     * Smooth angle approximation with maximum step limitation.
     */
    private float smoothAngle(float current, float target, float maxStep) {
        float diff = (float) class_3532.method_15338(target - current);
        if (Math.abs(diff) > maxStep) {
            diff = Math.copySign(maxStep, diff);
        }
        return current + diff;
    }
    
    public void reset() {
        currentYaw = -180.0F;
        currentPitch = 0.0F;
        godBridgeRightSide = false;
    }
}
