package lol.ethane.feature.module.defined.movement.noSlow.modes;

import lol.ethane.event.defined.player.PlayerSlowDownEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.noSlow.NoSlowModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class GrimNoSlow extends ModuleMode<NoSlowModule> {
   public GrimNoSlow(NoSlowModule module) {
      super(module);
   }

   @Subscribe
   private void onSlowDown(PlayerSlowDownEvent event) {
      if (this.mc.field_1724.field_6012 % 2 == 0) {
         event.setCancelled();
      }

   }

   public Enum<?> getValue() {
      return NoSlowModule.Mode.GRIM;
   }
}
