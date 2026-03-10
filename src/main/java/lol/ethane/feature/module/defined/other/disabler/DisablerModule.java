package lol.ethane.feature.module.defined.other.disabler;

import java.util.ArrayList;
import java.util.List;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.other.disabler.modes.CubeCraftDisabler;
import lol.ethane.feature.module.defined.other.disabler.modes.MiniBloxDisabler;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;
import net.fabricmc.loader.api.FabricLoader;

public class DisablerModule extends Module {
   private final ModeProperty<DisablerModule.Mode> modeProperty;
   private final CubeCraftDisabler cubeCraftDisabler;

   public DisablerModule() {
      super("Disabler", "Disables certain anti-cheat checks.", ModuleCategory.OTHER);
      boolean hasViaFabricPlus = FabricLoader.getInstance().isModLoaded("viafabricplus");
      DisablerModule.Mode[] modes;
      if (hasViaFabricPlus) {
         modes = DisablerModule.Mode.values();
      } else {
         modes = new DisablerModule.Mode[]{DisablerModule.Mode.CUBE$CRAFT};
      }

      this.modeProperty = new ModeProperty("Mode", this, DisablerModule.Mode.CUBE$CRAFT, modes);
      this.addProperties(new Property[]{this.modeProperty});
      this.cubeCraftDisabler = new CubeCraftDisabler(this);
      List<ModuleMode<DisablerModule>> moduleModes = new ArrayList();
      moduleModes.add(this.cubeCraftDisabler);
      if (hasViaFabricPlus) {
         moduleModes.add(new MiniBloxDisabler(this));
      }

      this.addModes(this.modeProperty, (ModuleMode[])moduleModes.toArray(new ModuleMode[0]));
   }

   public String getSuffix() {
      return this.modeProperty.getValue() == DisablerModule.Mode.CUBE$CRAFT ? "CubeCraft (" + String.valueOf(this.cubeCraftDisabler.typeProperty.getValue()) + ")" : StringUtil.normalizeEnumName(((DisablerModule.Mode)this.modeProperty.getValue()).toString());
   }

   public static enum Mode {
      CUBE$CRAFT,
      MINI$BLOX;

      // $FF: synthetic method
      private static DisablerModule.Mode[] $values() {
         return new DisablerModule.Mode[]{CUBE$CRAFT, MINI$BLOX};
      }
   }
}
