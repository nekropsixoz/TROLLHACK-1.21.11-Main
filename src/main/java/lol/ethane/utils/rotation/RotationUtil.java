package lol.ethane.utils.rotation;

import net.minecraft.class_241;
import net.minecraft.class_3532;

public class RotationUtil {
   public static float fov(class_241 current, class_241 other) {
      return distance(closest(current, other));
   }

   public static class_241 closest(class_241 current, class_241 other) {
      return new class_241(class_3532.method_15393(other.field_1343 - current.field_1343), other.field_1342 - current.field_1342);
   }

   private static float distance(class_241 rotation) {
      return class_3532.method_15355(rotation.field_1343 * rotation.field_1343 + rotation.field_1342 * rotation.field_1342);
   }

   public static float getRotationDifference(class_241 a, class_241 b) {
      return class_3532.method_15356(a.field_1343, b.field_1343) + Math.abs(a.field_1342 - b.field_1342);
   }

   public static double getCursorDelta(double rotationDelta, double sensitivityMultiplier) {
      return (double)((float)(rotationDelta / sensitivityMultiplier) / 0.15F);
   }
}
