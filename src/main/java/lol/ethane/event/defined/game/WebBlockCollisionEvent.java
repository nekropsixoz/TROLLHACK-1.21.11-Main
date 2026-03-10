package lol.ethane.event.defined.game;

import lol.ethane.event.EventCancellable;
import lombok.Generated;
import net.minecraft.class_2338;

public class WebBlockCollisionEvent extends EventCancellable {
   private final class_2338 pos;

   @Generated
   public WebBlockCollisionEvent(class_2338 pos) {
      this.pos = pos;
   }

   @Generated
   public class_2338 getPos() {
      return this.pos;
   }
}
