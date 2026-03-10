package lol.ethane.feature.module.defined.combat.criticals;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.combat.criticals.modes.PacketCriticals;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.misc.StringUtil;

public class CriticalsModule extends Module {
   private final ModeProperty<CriticalsModule.Mode> modeProperty;

   public CriticalsModule() {
      super("Criticals", "Deals critical's to the enemies using packets.", ModuleCategory.COMBAT);
      this.modeProperty = new ModeProperty("Mode", this, CriticalsModule.Mode.PACKET);
      this.addProperties(new Property[]{this.modeProperty});
      this.addModes(this.modeProperty, new ModuleMode[]{new PacketCriticals(this)});
   }

   public String getSuffix() {
      return StringUtil.normalizeEnumName(((CriticalsModule.Mode)this.modeProperty.getValue()).toString());
   }

   public static enum Mode {
      PACKET;

      // $FF: synthetic method
      private static CriticalsModule.Mode[] $values() {
         return new CriticalsModule.Mode[]{PACKET};
      }
   }
}
