package lol.ethane.feature.module.defined.player.nofall.modes;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.player.nofall.NoFallModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class VulcanNoFall extends ModuleMode<NoFallModule> {
   public VulcanNoFall(NoFallModule module) {
      super(module);
   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      if (this.mc.field_1724.field_6017 >= 3.0D) {
         event.setGround(true);
         this.mc.field_1724.method_18800(this.mc.field_1724.method_18798().field_1352, -0.09800000190735147D, this.mc.field_1724.method_18798().field_1350);
         this.mc.field_1724.field_6017 = 0.0D;
      }

   }

   public Enum<?> getValue() {
      return NoFallModule.Mode.VULCAN;
   }
}
