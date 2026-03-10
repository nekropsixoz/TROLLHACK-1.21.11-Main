package lol.ethane.utils.simulation;

import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_744;

public record DirectionalInput(boolean forwards, boolean backwards, boolean left, boolean right) {
   public static final DirectionalInput NONE = new DirectionalInput(false, false, false, false);

   public DirectionalInput(class_744 input) {
      this(input.field_54155.comp_3159(), input.field_54155.comp_3160(), input.field_54155.comp_3161(), input.field_54155.comp_3162());
   }

   public DirectionalInput(boolean forwards, boolean backwards, boolean left, boolean right) {
      this.forwards = forwards;
      this.backwards = backwards;
      this.left = left;
      this.right = right;
   }

   public static DirectionalInput fromMovement(float forward, float sideways) {
      return new DirectionalInput(forward > 0.0F, forward < 0.0F, sideways > 0.0F, sideways < 0.0F);
   }

   public static float getDegreesRelativeToView(class_243 velocity, float yaw) {
      double x = velocity.field_1352;
      double z = velocity.field_1350;
      double angle = Math.toDegrees(Math.atan2(-x, z));
      return (float)class_3532.method_15338(angle - (double)yaw);
   }

   public static DirectionalInput getDirectionalInputForDegrees(DirectionalInput current, float degrees) {
      boolean forwards = false;
      boolean backwards = false;
      boolean left = false;
      boolean right = false;
      if (degrees > -45.0F && degrees <= 45.0F) {
         forwards = true;
      } else if (degrees > 45.0F && degrees <= 135.0F) {
         left = true;
      } else if (degrees > -135.0F && degrees <= -45.0F) {
         right = true;
      } else {
         backwards = true;
      }

      if ((double)degrees > 22.5D && (double)degrees <= 67.5D) {
         forwards = true;
         left = true;
      } else if ((double)degrees > 112.5D && (double)degrees <= 157.5D) {
         backwards = true;
         left = true;
      } else if ((double)degrees > -157.5D && (double)degrees <= -112.5D) {
         backwards = true;
         right = true;
      } else if ((double)degrees > -67.5D && (double)degrees <= -22.5D) {
         forwards = true;
         right = true;
      }

      return new DirectionalInput(forwards, backwards, left, right);
   }

   public boolean forwards() {
      return this.forwards;
   }

   public boolean backwards() {
      return this.backwards;
   }

   public boolean left() {
      return this.left;
   }

   public boolean right() {
      return this.right;
   }
}
