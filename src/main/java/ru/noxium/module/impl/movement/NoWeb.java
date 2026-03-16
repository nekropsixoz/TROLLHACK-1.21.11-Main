package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.util.other.Mathf;
import ru.noxium.util.player.MoveUtil;
import ru.noxium.util.player.PlayerUtil;

@IModule(
   name = "No Web",
   description = "",
   category = Category.Movement,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class NoWeb extends Module {
   @EventInit
   public void onEvent(EventUpdate e) {
      if (!PlayerUtil.nullCheck() && PlayerUtil.isPlayerInWeb()) {
         double[] speed = MoveUtil.calculateDirection(Mathf.random(0.62F, 0.64F));
         mc.player.setVelocity(speed[0], mc.options.jumpKey.isPressed() ? 1.2 : (mc.options.sneakKey.isPressed() ? -2.0 : 0.0), speed[1]);
      }
   }
}
