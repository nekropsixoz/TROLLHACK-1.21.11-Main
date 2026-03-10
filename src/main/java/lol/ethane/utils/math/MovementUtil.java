package lol.ethane.utils.math;

import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.utils.simulation.DirectionalInput;
import net.minecraft.class_310;

public class MovementUtil {
   private static final class_310 mc = class_310.method_1551();

   public static boolean isMoving() {
      assert class_310.method_1551().field_1724 != null;

      return class_310.method_1551().field_1724.field_3913.method_3128().field_1343 != 0.0F || class_310.method_1551().field_1724.field_3913.method_3128().field_1342 != 0.0F;
   }

   public static float getMovementDirectionOfInput(float facingYaw, DirectionalInput input) {
      float actualYaw = facingYaw;
      float forward = 1.0F;

      assert mc.field_1724 != null;

      if (input.backwards()) {
         actualYaw = facingYaw + 180.0F;
         forward = -0.5F;
      } else if (input.forwards()) {
         forward = 0.5F;
      }

      if (input.left()) {
         actualYaw -= 90.0F * forward;
      }

      if (input.right()) {
         actualYaw += 90.0F * forward;
      }

      return actualYaw;
   }

   public static float getMovementDirectionRadians(float facingYaw) {
      float actualYaw = facingYaw;
      float forward = 1.0F;

      assert mc.field_1724 != null;

      if (mc.field_1724.field_3913.field_54155.comp_3160()) {
         actualYaw = facingYaw + 180.0F;
         forward = -0.5F;
      } else if (mc.field_1724.field_3913.field_54155.comp_3159()) {
         forward = 0.5F;
      }

      if (mc.field_1724.field_3913.field_54155.comp_3161()) {
         actualYaw -= 90.0F * forward;
      }

      if (mc.field_1724.field_3913.field_54155.comp_3162()) {
         actualYaw += 90.0F * forward;
      }

      return (float)Math.toRadians((double)actualYaw);
   }

   public static double speed() {
      return Math.hypot(mc.field_1724.method_18798().field_1352, mc.field_1724.method_18798().field_1350);
   }

   public static void strafe(double speed) {
      assert mc.field_1724 != null;

      float direction = getMovementDirectionRadians(RotationHelper.getClientHandler().getYawOr(mc.field_1724.method_36454()));
      if (isMoving()) {
         mc.field_1724.method_18800(-Math.sin((double)direction) * speed, mc.field_1724.method_18798().field_1351, Math.cos((double)direction) * speed);
      } else {
         mc.field_1724.method_18800(0.0D, mc.field_1724.method_18798().field_1351, 0.0D);
      }

   }

   public static void strafePercent(double percentage) {
      percentage /= 100.0D;
      percentage = Math.min(1.0D, Math.max(0.0D, percentage));

      assert mc.field_1724 != null;

      double motionX = mc.field_1724.method_18798().field_1352;
      double motionZ = mc.field_1724.method_18798().field_1350;
      strafe(speed());
      mc.field_1724.method_18800(motionX + (mc.field_1724.method_18798().field_1352 - motionX) * percentage, mc.field_1724.method_18798().field_1351, motionZ + (mc.field_1724.method_18798().field_1350 - motionZ) * percentage);
   }

   public static double getDirection(float rotationYaw, double moveForward, double moveStrafing) {
      if (moveForward < 0.0D) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (moveForward < 0.0D) {
         forward = -0.5F;
      } else if (moveForward > 0.0D) {
         forward = 0.5F;
      }

      if (moveStrafing > 0.0D) {
         rotationYaw -= 90.0F * forward;
      }

      if (moveStrafing < 0.0D) {
         rotationYaw += 90.0F * forward;
      }

      return Math.toRadians((double)rotationYaw);
   }
}
