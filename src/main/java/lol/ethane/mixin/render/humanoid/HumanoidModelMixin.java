package lol.ethane.mixin.render.humanoid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import lol.ethane.Ethane;
import lol.ethane.feature.module.defined.render.AnimationsModule;
import lol.ethane.utils.injection.HumanoidRenderStateExtension;
import net.minecraft.class_10034;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1819;
import net.minecraft.class_3881;
import net.minecraft.class_3882;
import net.minecraft.class_572;
import net.minecraft.class_583;
import net.minecraft.class_630;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_572.class})
public abstract class HumanoidModelMixin<T extends class_10034> extends class_583<T> implements class_3881<T>, class_3882 {
   private HumanoidModelMixin(class_630 modelPart) {
      super(modelPart);
   }

   @WrapOperation(
      method = {"method_54131"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3532;method_15363(FFF)F"
)}
   )
   private float fixPoseBlockingArm(float f, float g, float h, Operation<Float> original) {
      AnimationsModule module = (AnimationsModule)Ethane.getInstance().getModuleRepository().getModule(AnimationsModule.class);
      return module.isEnabled() ? 0.0F : (Float)original.call(new Object[]{f, g, h});
   }

   @WrapOperation(
      method = {"method_30155", "method_30154"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_572;method_54131(Lnet/minecraft/class_630;Z)V"
)}
   )
   private void fixPoseLeftRightArm(class_572<?> instance, class_630 modelPart, boolean bl, Operation<Void> original, @Local(argsOnly = true) T state) {
      original.call(new Object[]{instance, modelPart, bl});
      AnimationsModule module = (AnimationsModule)Ethane.getInstance().getModuleRepository().getModule(AnimationsModule.class);
      if (module.isEnabled()) {
         class_1309 entity = ((HumanoidRenderStateExtension)state).ethane$getEntity();
         if (entity instanceof class_1309 && state instanceof class_10034) {
            class_1799 stack = bl ? entity.method_61420(class_1306.field_6183) : entity.method_61420(class_1306.field_6182);
            if (!(stack.method_7909() instanceof class_1819)) {
               modelPart.field_3654 = modelPart.field_3654 * 0.5F - 0.62831855F;
               modelPart.field_3675 = 0.0F;
            }
         }
      }

   }
}
