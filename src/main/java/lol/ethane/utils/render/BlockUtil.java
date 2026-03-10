package lol.ethane.utils.render;

import lol.ethane.Ethane;
import lol.ethane.feature.module.defined.render.AnimationsModule;
import lol.ethane.utils.injection.HumanoidRenderStateExtension;
import net.minecraft.class_10034;
import net.minecraft.class_10426;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1819;
import net.minecraft.class_1839;
import net.minecraft.class_3489;
import net.minecraft.class_572.class_573;

public class BlockUtil {
   public static boolean isBlockUseState(class_1657 player) {
      return player.method_6047().method_31573(class_3489.field_42611) && player.method_6047().method_7976().equals(class_1839.field_8949) && player.method_6014() > 0;
   }

   public static boolean isForceBlockUseState(class_1657 player) {
      AnimationsModule animationsModule = (AnimationsModule)Ethane.getInstance().getModuleRepository().getModule(AnimationsModule.class);
      return player.method_6047().method_31573(class_3489.field_42611) && player.method_76694().method_7909() instanceof class_1819 && player.method_6014() > 0 && animationsModule.isEnabled() && !isBlockUseState(player);
   }

   public static boolean isThirdPersonBlockingState(class_10426 entityState) {
      AnimationsModule animationsModule = (AnimationsModule)Ethane.getInstance().getModuleRepository().getModule(AnimationsModule.class);
      if (!animationsModule.isEnabled()) {
         return false;
      } else if (!(entityState instanceof class_10034)) {
         return false;
      } else {
         class_10034 state = (class_10034)entityState;
         class_1309 livingEntity = ((HumanoidRenderStateExtension)state).ethane$getEntity();
         if (state.field_53414 && entityState.field_55303 == class_1306.field_6182 && (entityState.field_55304 == class_573.field_3409 || entityState.field_55304 == class_573.field_3406) && livingEntity.method_5998(class_1268.field_5808).method_7909() instanceof class_1819 && livingEntity.method_5998(class_1268.field_5810).method_31573(class_3489.field_42611)) {
            return true;
         } else if (state.field_53414 && entityState.field_55303 == class_1306.field_6183 && (entityState.field_55306 == class_573.field_3409 || entityState.field_55306 == class_573.field_3406) && livingEntity.method_5998(class_1268.field_5810).method_7909() instanceof class_1819 && livingEntity.method_5998(class_1268.field_5808).method_31573(class_3489.field_42611)) {
            return true;
         } else {
            if (livingEntity instanceof class_1657) {
               class_1657 player = (class_1657)livingEntity;
               if (isBlockUseState(player) || isForceBlockUseState(player)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
