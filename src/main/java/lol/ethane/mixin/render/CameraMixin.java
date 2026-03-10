package lol.ethane.mixin.render;

import lol.ethane.Ethane;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.defined.render.CameraClipModule;
import net.minecraft.class_1297;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4184.class})
public abstract class CameraMixin {
   @Redirect(
      method = {"method_19321"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5705(F)F"
)
   )
   private float redirectYaw(class_1297 instance, float tickDelta) {
      return RotationHelper.getClientHandler().getYawOr(instance.method_5705(tickDelta));
   }

   @Redirect(
      method = {"method_19321"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5695(F)F"
)
   )
   private float redirectPitch(class_1297 instance, float tickDelta) {
      return RotationHelper.getClientHandler().getPitchOr(instance.method_5695(tickDelta));
   }

   @Inject(
      method = {"method_19318"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getMaxZoom(float f, CallbackInfoReturnable<Float> cir) {
      CameraClipModule cameraClipModule = (CameraClipModule)Ethane.getInstance().getModuleRepository().getModule(CameraClipModule.class);
      if (cameraClipModule.isEnabled()) {
         cir.setReturnValue(f);
      }

   }
}
