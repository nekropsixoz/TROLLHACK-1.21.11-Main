package lol.ethane.event.defined.render;

import lombok.Generated;
import net.minecraft.class_332;

public class RenderBloomEvent {
   private final class_332 graphics;
   private final float delta;

   @Generated
   public class_332 getGraphics() {
      return this.graphics;
   }

   @Generated
   public float getDelta() {
      return this.delta;
   }

   @Generated
   public RenderBloomEvent(class_332 graphics, float delta) {
      this.graphics = graphics;
      this.delta = delta;
   }
}
