package lol.ethane.mixin.render;

import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import net.minecraft.class_746;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_759.class})
public class ItemInHandRendererMixin {
   @Redirect(
      method = {"method_22976"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_5705(F)F"
)
   )
   private float redirectItemYaw(class_746 instance, float tickDelta) {
      return RotationHelper.getClientHandler().getYawOr(instance.method_5705(tickDelta));
   }

   @Redirect(
      method = {"method_22976"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_5695(F)F"
)
   )
   private float redirectItemPitch(class_746 instance, float tickDelta) {
      return RotationHelper.getClientHandler().getPitchOr(instance.method_5695(tickDelta));
   }

   @Redirect(
      method = {"method_22976"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_746;field_3931:F",
   opcode = 180
)
   )
   private float redirectItemLastRenderYaw(class_746 instance) {
      return RotationHelper.getClientHandler().getLastRenderYawOr(instance.field_3931);
   }

   @Redirect(
      method = {"method_22976"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_746;field_3914:F",
   opcode = 180
)
   )
   private float redirectItemLastRenderPitch(class_746 instance) {
      return RotationHelper.getClientHandler().getLastRenderPitchOr(instance.field_3914);
   }

   @Redirect(
      method = {"method_22976"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_746;field_3932:F",
   opcode = 180
)
   )
   private float redirectItemRenderYaw(class_746 instance) {
      return RotationHelper.getClientHandler().getRenderYawOr(instance.field_3932);
   }

   @Redirect(
      method = {"method_22976"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_746;field_3916:F",
   opcode = 180
)
   )
   private float redirectItemRenderPitch(class_746 instance) {
      return RotationHelper.getClientHandler().getRenderPitchOr(instance.field_3916);
   }
}
