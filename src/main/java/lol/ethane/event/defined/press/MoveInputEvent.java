package lol.ethane.event.defined.press;

import lol.ethane.utils.simulation.DirectionalInput;
import lombok.Generated;

public class MoveInputEvent {
   private float forward;
   private float sideways;
   private boolean jump;
   private boolean sneak;
   private boolean sprint;

   public DirectionalInput directionalInput() {
      return DirectionalInput.fromMovement(this.forward, this.sideways);
   }

   @Generated
   public MoveInputEvent(float forward, float sideways, boolean jump, boolean sneak, boolean sprint) {
      this.forward = forward;
      this.sideways = sideways;
      this.jump = jump;
      this.sneak = sneak;
      this.sprint = sprint;
   }

   @Generated
   public void setForward(float forward) {
      this.forward = forward;
   }

   @Generated
   public void setSideways(float sideways) {
      this.sideways = sideways;
   }

   @Generated
   public void setJump(boolean jump) {
      this.jump = jump;
   }

   @Generated
   public void setSneak(boolean sneak) {
      this.sneak = sneak;
   }

   @Generated
   public void setSprint(boolean sprint) {
      this.sprint = sprint;
   }

   @Generated
   public float getForward() {
      return this.forward;
   }

   @Generated
   public float getSideways() {
      return this.sideways;
   }

   @Generated
   public boolean isJump() {
      return this.jump;
   }

   @Generated
   public boolean isSneak() {
      return this.sneak;
   }

   @Generated
   public boolean isSprint() {
      return this.sprint;
   }
}
