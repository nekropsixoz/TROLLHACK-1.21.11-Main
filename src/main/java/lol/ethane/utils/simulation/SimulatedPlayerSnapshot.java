package lol.ethane.utils.simulation;

import net.minecraft.class_243;

public record SimulatedPlayerSnapshot(class_243 pos, double fallDistance, class_243 velocity, boolean onGround, boolean clipLedged) {
   public SimulatedPlayerSnapshot(SimulatedPlayer s) {
      this(s.pos, s.fallDistance, s.deltaMovement, s.onGround, s.isClipLedged());
   }

   public SimulatedPlayerSnapshot(class_243 pos, double fallDistance, class_243 velocity, boolean onGround, boolean clipLedged) {
      this.pos = pos;
      this.fallDistance = fallDistance;
      this.velocity = velocity;
      this.onGround = onGround;
      this.clipLedged = clipLedged;
   }

   public class_243 pos() {
      return this.pos;
   }

   public double fallDistance() {
      return this.fallDistance;
   }

   public class_243 velocity() {
      return this.velocity;
   }

   public boolean onGround() {
      return this.onGround;
   }

   public boolean clipLedged() {
      return this.clipLedged;
   }
}
