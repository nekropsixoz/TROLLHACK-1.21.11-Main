package lol.ethane.event.defined.mouse;

import lombok.Generated;

public class MouseUpdateEvent {
   private double deltaX;
   private double deltaY;
   private final double sensitivityMultiplier;
   private final boolean unlockCursorRun;
   private boolean handled;

   @Generated
   public MouseUpdateEvent(double deltaX, double deltaY, double sensitivityMultiplier, boolean unlockCursorRun, boolean handled) {
      this.deltaX = deltaX;
      this.deltaY = deltaY;
      this.sensitivityMultiplier = sensitivityMultiplier;
      this.unlockCursorRun = unlockCursorRun;
      this.handled = handled;
   }

   @Generated
   public double getDeltaX() {
      return this.deltaX;
   }

   @Generated
   public double getDeltaY() {
      return this.deltaY;
   }

   @Generated
   public double getSensitivityMultiplier() {
      return this.sensitivityMultiplier;
   }

   @Generated
   public boolean isUnlockCursorRun() {
      return this.unlockCursorRun;
   }

   @Generated
   public boolean isHandled() {
      return this.handled;
   }

   @Generated
   public void setDeltaX(double deltaX) {
      this.deltaX = deltaX;
   }

   @Generated
   public void setDeltaY(double deltaY) {
      this.deltaY = deltaY;
   }

   @Generated
   public void setHandled(boolean handled) {
      this.handled = handled;
   }
}
