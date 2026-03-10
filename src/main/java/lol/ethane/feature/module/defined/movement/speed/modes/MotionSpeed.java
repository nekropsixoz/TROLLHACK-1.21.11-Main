package lol.ethane.feature.module.defined.movement.speed.modes;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.speed.SpeedModule;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MovementUtil;

public class MotionSpeed extends ModuleMode<SpeedModule> {
   private final Property<Double> speedProperty = new NumberProperty("Speed", this, 0.25D, 0.1D, 1.5D, 0.01D);

   public MotionSpeed(SpeedModule module) {
      super(module);
   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      if (MovementUtil.isMoving()) {
         if (this.mc.field_1724.method_24828()) {
            this.mc.field_1724.method_6043();
         }

         MovementUtil.strafe((Double)this.speedProperty.getValue());
      }

   }

   public Enum<?> getValue() {
      return SpeedModule.Mode.MOTION;
   }
}
