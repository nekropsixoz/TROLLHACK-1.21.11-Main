package lol.ethane.feature.module.defined.player.nofall;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.player.nofall.modes.CubeCraftNoFall;
import lol.ethane.feature.module.defined.player.nofall.modes.SpoofNoFall;
import lol.ethane.feature.module.defined.player.nofall.modes.VulcanNoFall;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;

public class NoFallModule extends Module {
   private final ModeProperty<NoFallModule.Mode> modeProperty;

   public NoFallModule() {
      super("No Fall", "Prevents you from taking fall damage.", ModuleCategory.PLAYER);
      this.modeProperty = new ModeProperty("Mode", this, NoFallModule.Mode.CUBE$CRAFT);
      this.addProperties(new Property[]{this.modeProperty});
      this.addModes(this.modeProperty, new ModuleMode[]{new SpoofNoFall(this), new CubeCraftNoFall(this), new VulcanNoFall(this)});
   }

   public String getSuffix() {
      return StringUtil.normalizeEnumName(((NoFallModule.Mode)this.modeProperty.getValue()).toString());
   }

   public static enum Mode {
      SPOOF,
      CUBE$CRAFT,
      VULCAN;

      // $FF: synthetic method
      private static NoFallModule.Mode[] $values() {
         return new NoFallModule.Mode[]{SPOOF, CUBE$CRAFT, VULCAN};
      }
   }
}
