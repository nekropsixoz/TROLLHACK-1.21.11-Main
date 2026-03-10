package lol.ethane.event.defined.press;

import lombok.Generated;

class LWJGLInteractionEvent {
   private final int interactionCode;

   protected LWJGLInteractionEvent(int interactionCode) {
      this.interactionCode = interactionCode;
   }

   @Generated
   public int getInteractionCode() {
      return this.interactionCode;
   }
}
