package lol.ethane.feature.module.defined.player.nofall.modes;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.player.nofall.NoFallModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class SpoofNoFall extends ModuleMode<NoFallModule> {
   public SpoofNoFall(NoFallModule module) {
      super(module);
   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      event.setGround(true);
   }

   public Enum<?> getValue() {
      return NoFallModule.Mode.SPOOF;
   }
}
