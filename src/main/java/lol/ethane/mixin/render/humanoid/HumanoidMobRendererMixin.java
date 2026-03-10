package lol.ethane.mixin.render.humanoid;

import lol.ethane.utils.injection.HumanoidRenderStateExtension;
import net.minecraft.class_10034;
import net.minecraft.class_10442;
import net.minecraft.class_1309;
import net.minecraft.class_909;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_909.class})
public class HumanoidMobRendererMixin {
   @Inject(
      method = {"method_62461"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_10426;method_65577(Lnet/minecraft/class_1309;Lnet/minecraft/class_10426;Lnet/minecraft/class_10442;F)V",
   shift = Shift.AFTER
)}
   )
   private static void updateEntity(class_1309 livingEntity, class_10034 humanoidRenderState, float f, class_10442 itemModelResolver, CallbackInfo ci) {
      ((HumanoidRenderStateExtension)humanoidRenderState).ethane$setEntity(livingEntity);
   }
}
