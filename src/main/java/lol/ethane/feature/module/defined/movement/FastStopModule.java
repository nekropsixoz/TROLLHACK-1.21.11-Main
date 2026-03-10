package lol.ethane.feature.module.defined.movement;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.utils.math.MovementUtil;

public class FastStopModule extends Module {
   private final Property<Boolean> groundOnly = new BooleanProperty("Ground only", false);

   public FastStopModule() {
      super("Fast Stop", "Stops you instantly.", ModuleCategory.MOVEMENT);
      this.addProperties(new Property[]{this.groundOnly});
   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      if (!MovementUtil.isMoving() && (!(Boolean)this.groundOnly.getValue() || this.mc.field_1724.method_24828())) {
         this.mc.field_1724.method_18800(0.0D, this.mc.field_1724.method_18798().field_1351, 0.0D);
      }

   }
}
