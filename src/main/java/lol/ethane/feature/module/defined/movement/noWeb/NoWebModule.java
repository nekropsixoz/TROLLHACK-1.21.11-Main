package lol.ethane.feature.module.defined.movement.noWeb;

import lol.ethane.event.defined.game.WebBlockCollisionEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.utils.math.MovementUtil;

public class NoWebModule extends Module {
   private final Property<Boolean> grim = new BooleanProperty("Grim", false);

   public NoWebModule() {
      super("No Web", "Disables web collision so you don't get slowed down.", ModuleCategory.MOVEMENT);
      this.addProperties(new Property[]{this.grim});
   }

   @Subscribe
   private void onCollision(WebBlockCollisionEvent e) {
      if ((Boolean)this.grim.getValue()) {
         MovementUtil.strafe(0.6D);
      } else {
         e.setCancelled();
      }
   }
}
