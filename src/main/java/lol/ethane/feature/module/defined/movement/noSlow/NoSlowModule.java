package lol.ethane.feature.module.defined.movement.noSlow;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.movement.noSlow.modes.GrimNoSlow;
import lol.ethane.feature.module.defined.movement.noSlow.modes.VanillaNoSlow;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;

public class NoSlowModule extends Module {
   private final ModeProperty<NoSlowModule.Mode> modeProperty;

   public NoSlowModule() {
      super("No Slow", "Prevents you from being slowed down while eating", ModuleCategory.MOVEMENT);
      this.modeProperty = new ModeProperty("Mode", this, NoSlowModule.Mode.VANILLA);
      this.addProperties(new Property[]{this.modeProperty});
      this.addModes(this.modeProperty, new ModuleMode[]{new VanillaNoSlow(this), new GrimNoSlow(this)});
   }

   public String getSuffix() {
      return StringUtil.normalizeEnumName(((NoSlowModule.Mode)this.modeProperty.getValue()).toString());
   }

   public static enum Mode {
      VANILLA,
      GRIM;

      // $FF: synthetic method
      private static NoSlowModule.Mode[] $values() {
         return new NoSlowModule.Mode[]{VANILLA, GRIM};
      }
   }
}
