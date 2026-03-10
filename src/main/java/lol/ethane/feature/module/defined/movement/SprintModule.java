package lol.ethane.feature.module.defined.movement;

import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;

public class SprintModule extends Module {
   public SprintModule() {
      super("Sprint", "Automatically sprints.", ModuleCategory.MOVEMENT);
   }

   @Subscribe
   private void onInput(MoveInputEvent event) {
      event.setSprint(true);
   }
}
