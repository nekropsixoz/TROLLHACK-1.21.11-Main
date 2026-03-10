package lol.ethane.feature.module.defined.movement.flight.modes;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.flight.FlightModule;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MovementUtil;

public class MotionFlight extends ModuleMode<FlightModule> {
   private final Property<Double> speedProperty = new NumberProperty("Speed", this, 0.25D, 0.1D, 1.5D, 0.01D);

   public MotionFlight(FlightModule module) {
      super(module);
   }

   @Subscribe
   private void onMotion(PlayerMovementTickEvent event) {
      this.mc.field_1724.method_18800(this.mc.field_1724.method_18798().field_1352, this.mc.field_1690.field_1903.method_1434() ? (Double)this.speedProperty.getValue() / 2.0D : (this.mc.field_1690.field_1832.method_1434() ? -(Double)this.speedProperty.getValue() / 2.0D : 0.0D), this.mc.field_1724.method_18798().field_1350);
      MovementUtil.strafe((Double)this.speedProperty.getValue());
   }

   public Enum<?> getValue() {
      return FlightModule.Mode.MOTION;
   }
}
