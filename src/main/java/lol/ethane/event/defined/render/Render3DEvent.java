package lol.ethane.event.defined.render;

import lombok.Generated;
import net.minecraft.class_4587;
import net.minecraft.class_9779;

public class Render3DEvent {
   private final class_4587 poseStack;
   private final class_9779 deltaTracker;

   @Generated
   public class_4587 getPoseStack() {
      return this.poseStack;
   }

   @Generated
   public class_9779 getDeltaTracker() {
      return this.deltaTracker;
   }

   @Generated
   public Render3DEvent(class_4587 poseStack, class_9779 deltaTracker) {
      this.poseStack = poseStack;
      this.deltaTracker = deltaTracker;
   }
}
