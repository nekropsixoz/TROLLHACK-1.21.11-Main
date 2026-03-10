package lol.ethane.event.defined.combat;

import lol.ethane.event.EventCancellable;
import lombok.Generated;
import net.minecraft.class_1297;

public class AttackEvent extends EventCancellable {
   private final class_1297 target;

   @Generated
   public class_1297 getTarget() {
      return this.target;
   }

   @Generated
   public AttackEvent(class_1297 target) {
      this.target = target;
   }
}
