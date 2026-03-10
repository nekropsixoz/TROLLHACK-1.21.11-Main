package lol.ethane.feature.module.defined.other;

import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;

public class AntiLogBypassModule extends Module {
   public AntiLogBypassModule() {
      super("Anti Log Bypass", "A", ModuleCategory.OTHER);
   }

   protected void onEnable() {
      ((lol.ethane.mixin.accessor.ClientConnectionAccessor)this.mc.method_1562().method_48296()).getChannel().config().setConnectTimeoutMillis(0);
   }
}
