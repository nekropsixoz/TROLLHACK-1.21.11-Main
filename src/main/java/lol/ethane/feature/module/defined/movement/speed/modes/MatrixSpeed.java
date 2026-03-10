package lol.ethane.feature.module.defined.movement.speed.modes;

import java.util.Arrays;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.defined.movement.speed.SpeedModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MovementUtil;
import net.minecraft.class_304;
import net.minecraft.class_3532;

public class MatrixSpeed extends ModuleMode<SpeedModule> {
   private int airTicks;

   public MatrixSpeed(SpeedModule module) {
      super(module);
   }

   @Subscribe
   private void onInput(MoveInputEvent event) {
      if (MovementUtil.isMoving()) {
         event.setSneak(false);
         event.setJump(true);
      }

   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      if (this.mc.field_1724.method_24828()) {
         this.airTicks = 0;
      } else {
         ++this.airTicks;
      }

      if (this.mc.field_1724.method_23318() % 0.015625D == 0.0D || MovementUtil.speed() < 0.2D) {
         MovementUtil.strafe(MovementUtil.speed());
      }

      if (MovementUtil.speed() < 0.195D && !this.mc.field_1724.method_6115()) {
         MovementUtil.strafe(0.195D);
      }

      if (this.mc.field_1724.method_24828()) {
         this.mc.field_1724.method_18799(this.mc.field_1724.method_18798().method_18805(1.001D, 1.0D, 1.001D));
         MovementUtil.strafe(MovementUtil.speed());
      }

      class_304[] gameSettings = new class_304[]{this.mc.field_1690.field_1894, this.mc.field_1690.field_1849, this.mc.field_1690.field_1881, this.mc.field_1690.field_1913};
      int[] down = new int[]{0};
      Arrays.stream(gameSettings).forEach((keyBinding) -> {
         down[0] += keyBinding.method_1434() ? 1 : 0;
      });
      boolean active = down[0] == 1;
      if (active) {
         double groundIncrease = 0.0026000750109401644D;
         double airIncrease = 5.199896488849598E-4D;
         double increase = this.mc.field_1724.method_24828() ? 0.0026000750109401644D : 5.199896488849598E-4D;
         if (MovementUtil.isMoving()) {
            double yaw = (double)MovementUtil.getMovementDirectionRadians(RotationHelper.getClientHandler().getYawOr(this.mc.field_1724.method_36454()));
            this.mc.field_1724.method_18799(this.mc.field_1724.method_18798().method_1031((double)(-class_3532.method_15374((double)((float)yaw))) * increase, 0.0D, (double)class_3532.method_15362((double)((float)yaw)) * increase));
         }
      }
   }

   public Enum<?> getValue() {
      return SpeedModule.Mode.MATRIX;
   }
}
