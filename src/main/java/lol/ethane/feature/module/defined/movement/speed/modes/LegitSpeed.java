package lol.ethane.feature.module.defined.movement.speed.modes;

import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.speed.SpeedModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MovementUtil;

public class LegitSpeed extends ModuleMode<SpeedModule> {
   public LegitSpeed(SpeedModule module) {
      super(module);
   }

   @Subscribe
   private void onInput(MoveInputEvent event) {
      if (MovementUtil.isMoving()) {
         event.setJump(true);
      }

   }

   public Enum<?> getValue() {
      return SpeedModule.Mode.LEGIT;
   }
}
