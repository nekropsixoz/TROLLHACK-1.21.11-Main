package lol.ethane.feature.module.defined.movement.noSlow.modes;

import lol.ethane.event.defined.player.PlayerSlowDownEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.noSlow.NoSlowModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class VanillaNoSlow extends ModuleMode<NoSlowModule> {
   public VanillaNoSlow(NoSlowModule module) {
      super(module);
   }

   @Subscribe
   private void onSlowDown(PlayerSlowDownEvent event) {
      event.setCancelled();
   }

   public Enum<?> getValue() {
      return NoSlowModule.Mode.VANILLA;
   }
}
