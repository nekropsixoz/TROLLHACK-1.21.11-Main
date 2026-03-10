package lol.ethane.feature.module.defined.movement.flight;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.movement.flight.modes.HycraftDamageFlight;
import lol.ethane.feature.module.defined.movement.flight.modes.MotionFlight;
import lol.ethane.feature.module.defined.movement.flight.modes.PolarFlight;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;

public class FlightModule extends Module {
   private final ModeProperty<FlightModule.Mode> modeProperty;

   public FlightModule() {
      super("Flight", "zoomies", ModuleCategory.MOVEMENT);
      this.modeProperty = new ModeProperty("Mode", this, FlightModule.Mode.MOTION);
      this.addProperties(new Property[]{this.modeProperty});
      this.addModes(this.modeProperty, new ModuleMode[]{new MotionFlight(this), new PolarFlight(this), new HycraftDamageFlight(this)});
   }

   public String getSuffix() {
      return StringUtil.normalizeEnumName(((FlightModule.Mode)this.modeProperty.getValue()).toString());
   }

   public static enum Mode {
      MOTION,
      POLAR,
      HYCRAFT_DAMAGE;

      // $FF: synthetic method
      private static FlightModule.Mode[] $values() {
         return new FlightModule.Mode[]{MOTION, POLAR, HYCRAFT_DAMAGE};
      }
   }
}
