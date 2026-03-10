package lol.ethane.feature.module.defined.movement.speed.modes;

import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.event.defined.player.PlayerJumpEvent;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.defined.movement.speed.SpeedModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MovementUtil;

public class PolarSpeed extends ModuleMode<SpeedModule> {
   private int jumpTimes;
   private int airTicks;

   public PolarSpeed(SpeedModule module) {
      super(module);
   }

   public void onEnable() {
      this.jumpTimes = 0;
      super.onEnable();
   }

   @Subscribe
   private void onInput(MoveInputEvent event) {
      if (MovementUtil.isMoving()) {
         event.setJump(true);
      }

   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      if (this.mc.field_1724.method_24828()) {
         this.airTicks = 0;
      } else {
         ++this.airTicks;
         if (this.airTicks == 1) {
            float value = ThreadLocalRandom.current().nextFloat(0.0F, this.jumpTimes % 10 == 0 ? 0.01F : (this.jumpTimes % 2 == 0 ? 0.007F : 1.0E-4F));
            float direction = MovementUtil.getMovementDirectionRadians(RotationHelper.getClientHandler().getYawOr(this.mc.field_1724.method_36454()));
            this.mc.field_1724.method_18799(this.mc.field_1724.method_18798().method_1031(-Math.sin((double)direction) * (double)value, 0.0D, Math.cos((double)direction) * (double)value));
         }

         if (this.airTicks == 5 && this.jumpTimes % 4 == 0) {
            this.mc.field_1724.method_18799(this.mc.field_1724.method_18798().method_1031(0.0D, -0.025D, 0.0D));
         }

      }
   }

   @Subscribe
   private void onJump(PlayerJumpEvent event) {
      ++this.jumpTimes;
   }

   public Enum<?> getValue() {
      return SpeedModule.Mode.POLAR;
   }
}
