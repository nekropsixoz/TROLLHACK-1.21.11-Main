package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.util.player.MoveUtil;
import ru.noxium.util.player.PlayerUtil;

@IModule(
   name = "Strafe",
   description = "Улучшенное движение стрейфа",
   category = Category.Movement,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class Strafe extends Module {
   public static ModeSetting mode = new ModeSetting("Режим", "Matrix", "Matrix", "Grim");
   public static SliderSetting speed = new SliderSetting("Скорость", 0.42F, 0F, 1F, 0.01F, false);

   public Strafe() {
      this.addSettings(new Setting[]{mode, speed});
   }

   @EventInit
   public void onUpdate(EventUpdate event) {
      if (PlayerUtil.nullCheck()) return;

      boolean moving = MoveUtil.isMoving();
      float yaw = mc.player.getYaw();

      if (mode.is("Matrix")) {
         handleMatrixMode(moving, yaw);
      } else if (mode.is("Grim")) {
         handleGrimMode(moving, yaw);
      }
   }

   private void handleMatrixMode(boolean moving, float yaw) {
      if (moving) {
         double[] direction = MoveUtil.calculateDirection(speed.get() * 1.5);
         mc.player.setVelocity(direction[0], mc.player.getVelocity().y, direction[1]);
      } else {
         mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
      }
   }

   private void handleGrimMode(boolean moving, float yaw) {
      if (moving && HitAura.target == null) {
         // Simple strafe when not in combat
         double[] direction = MoveUtil.calculateDirection(speed.get());
         mc.player.setVelocity(direction[0], mc.player.getVelocity().y, direction[1]);
         
         // Adjust rotation to match movement direction
         double moveYaw = MoveUtil.direction(yaw, 
            mc.options.forwardKey.isPressed() ? 1 : 0, 
            mc.options.leftKey.isPressed() ? 1 : (mc.options.rightKey.isPressed() ? -1 : 0)
         );
         float targetYaw = (float) Math.toDegrees(moveYaw);
         mc.player.setYaw(targetYaw);
      }
   }
}
