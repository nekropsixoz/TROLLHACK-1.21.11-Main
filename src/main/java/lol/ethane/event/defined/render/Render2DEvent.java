package lol.ethane.event.defined.render;

import lol.aether.shader.MgfxContext;
import lombok.Generated;
import net.minecraft.class_332;

public class Render2DEvent {
   private final MgfxContext context;
   private final class_332 graphics;
   private final float delta;

   @Generated
   public MgfxContext getContext() {
      return this.context;
   }

   @Generated
   public class_332 getGraphics() {
      return this.graphics;
   }

   @Generated
   public float getDelta() {
      return this.delta;
   }

   @Generated
   public Render2DEvent(MgfxContext context, class_332 graphics, float delta) {
      this.context = context;
      this.graphics = graphics;
      this.delta = delta;
   }
}
