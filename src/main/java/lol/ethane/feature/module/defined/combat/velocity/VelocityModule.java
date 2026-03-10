package lol.ethane.feature.module.defined.combat.velocity;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.combat.velocity.modes.CancelVelocity;
import lol.ethane.feature.module.defined.combat.velocity.modes.CustomVelocity;
import lol.ethane.feature.module.defined.combat.velocity.modes.JumpResetVelocity;
import lol.ethane.feature.module.defined.combat.velocity.modes.ReversedVelocity;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;

public class VelocityModule extends Module {
   private final ModeProperty<VelocityModule.Mode> modeProperty;

   public VelocityModule() {
      super("Velocity", "Modifies your knockback", ModuleCategory.COMBAT);
      this.modeProperty = new ModeProperty("Mode", this, VelocityModule.Mode.CANCEL);
      this.addProperties(new Property[]{this.modeProperty});
      this.addModes(this.modeProperty, new ModuleMode[]{new CancelVelocity(this), new ReversedVelocity(this), new CustomVelocity(this), new JumpResetVelocity(this)});
   }

   public String getSuffix() {
      return ((VelocityModule.Mode)this.modeProperty.getValue()).toString();
   }

   public static enum Mode {
      CANCEL,
      REVERSED,
      CUSTOM,
      JUMP_$RESET;

      public String toString() {
         return StringUtil.normalizeEnumName(this.name());
      }

      // $FF: synthetic method
      private static VelocityModule.Mode[] $values() {
         return new VelocityModule.Mode[]{CANCEL, REVERSED, CUSTOM, JUMP_$RESET};
      }
   }
}
