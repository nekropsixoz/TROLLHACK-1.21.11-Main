package lol.ethane.feature.module.defined.movement.speed;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.movement.speed.modes.LegitSpeed;
import lol.ethane.feature.module.defined.movement.speed.modes.MatrixSpeed;
import lol.ethane.feature.module.defined.movement.speed.modes.MotionSpeed;
import lol.ethane.feature.module.defined.movement.speed.modes.PolarSpeed;
import lol.ethane.feature.module.defined.movement.speed.modes.VerusSpeed;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;

public class SpeedModule extends Module {
   private final ModeProperty<SpeedModule.Mode> modeProperty;

   public SpeedModule() {
      super("Speed", "zoomies", ModuleCategory.MOVEMENT);
      this.modeProperty = new ModeProperty("Mode", this, SpeedModule.Mode.LEGIT);
      this.addProperties(new Property[]{this.modeProperty});
      this.addModes(this.modeProperty, new ModuleMode[]{new MotionSpeed(this), new VerusSpeed(this), new LegitSpeed(this), new MatrixSpeed(this), new PolarSpeed(this)});
   }

   public String getSuffix() {
      return StringUtil.normalizeEnumName(((SpeedModule.Mode)this.modeProperty.getValue()).toString());
   }

   public static enum Mode {
      MOTION,
      VERUS,
      LEGIT,
      MATRIX,
      POLAR;

      // $FF: synthetic method
      private static SpeedModule.Mode[] $values() {
         return new SpeedModule.Mode[]{MOTION, VERUS, LEGIT, MATRIX, POLAR};
      }
   }
}
