package lol.ethane.feature.module.defined.movement;

import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.utils.math.MovementUtil;
import net.minecraft.class_3532;

public class MovementFixModule extends Module {
   public MovementFixModule() {
      super("Movement Fix", "Locks your movement to your rotations.", ModuleCategory.MOVEMENT);
   }

   @Subscribe
   public void onMoveInput(MoveInputEvent event) {
      float forward = event.getForward();
      float strafe = event.getSideways();
      if (forward != 0.0F || strafe != 0.0F) {
         float angle = (float)Math.toDegrees(MovementUtil.getDirection(RotationHelper.getClientHandler().getYawOr(this.mc.field_1724.method_36454()), (double)forward, (double)strafe));
         float closestForward = 0.0F;
         float closestSideways = 0.0F;
         float closestDifference = Float.MAX_VALUE;

         for(float predictedForward = -1.0F; predictedForward <= 1.0F; ++predictedForward) {
            for(float predictedStrafe = -1.0F; predictedStrafe <= 1.0F; ++predictedStrafe) {
               if (predictedStrafe != 0.0F || predictedForward != 0.0F) {
                  float predictedAngle = (float)Math.toDegrees(MovementUtil.getDirection(this.mc.field_1724.method_36454(), (double)predictedForward, (double)predictedStrafe));
                  double difference = (double)class_3532.method_15356(angle, predictedAngle);
                  if (difference < (double)closestDifference) {
                     closestDifference = (float)difference;
                     closestForward = predictedForward;
                     closestSideways = predictedStrafe;
                  }
               }
            }
         }

         event.setForward(closestForward);
         event.setSideways(closestSideways);
      }
   }
}
