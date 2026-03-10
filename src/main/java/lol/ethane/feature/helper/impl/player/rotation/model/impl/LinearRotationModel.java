package lol.ethane.feature.helper.impl.player.rotation.model.impl;

import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.feature.helper.impl.player.rotation.model.IRotationModel;
import net.minecraft.class_241;
import net.minecraft.class_3532;

public class LinearRotationModel implements IRotationModel {
   public class_241 tick(class_241 from, class_241 to, float delta) {
      float deltaYaw = class_3532.method_15393(to.field_1343 - from.field_1343) * delta;
      float deltaPitch = (to.field_1342 - from.field_1342) * delta;
      double distance = Math.sqrt((double)(deltaYaw * deltaYaw + deltaPitch * deltaPitch));
      if (distance == 0.0D) {
         return new class_241(from.field_1343 + deltaYaw, from.field_1342 + deltaPitch);
      } else {
         double distributionYaw = Math.abs((double)deltaYaw / distance);
         double distributionPitch = Math.abs((double)deltaPitch / distance);
         double maxYaw = ThreadLocalRandom.current().nextDouble(40.0D, 120.0D) * distributionYaw;
         double maxPitch = ThreadLocalRandom.current().nextDouble(40.0D, 120.0D) * distributionPitch;
         float moveYaw = (float)Math.max(Math.min((double)deltaYaw, maxYaw), -maxYaw);
         float movePitch = (float)Math.max(Math.min((double)deltaPitch, maxPitch), -maxPitch);
         return new class_241(from.field_1343 + moveYaw, from.field_1342 + movePitch);
      }
   }
}
