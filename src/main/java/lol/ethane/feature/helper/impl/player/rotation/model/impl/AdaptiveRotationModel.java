package lol.ethane.feature.helper.impl.player.rotation.model.impl;

import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.feature.helper.impl.player.rotation.model.IRotationModel;
import net.minecraft.class_241;
import net.minecraft.class_3532;

public class AdaptiveRotationModel implements IRotationModel {
   private float lastVelocity;

   public class_241 tick(class_241 from, class_241 to, float delta) {
      float deltaYaw = class_3532.method_15393(to.field_1343 - from.field_1343);
      float deltaPitch = to.field_1342 - from.field_1342;
      float distance = class_3532.method_15355(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
      if (distance == 0.0F) {
         this.lastVelocity = 0.0F;
         return to;
      } else {
         float targetVelocity = Math.min(distance, 15.0F + ThreadLocalRandom.current().nextFloat() * 10.0F);
         float acceleration = 2.0F + ThreadLocalRandom.current().nextFloat() * 2.0F;
         float velocity = this.lastVelocity + (targetVelocity - this.lastVelocity) * 0.2F * acceleration;
         this.lastVelocity = velocity;
         float moveYaw = deltaYaw / distance * velocity;
         float movePitch = deltaPitch / distance * velocity;
         return new class_241(from.field_1343 + moveYaw, from.field_1342 + movePitch);
      }
   }
}
