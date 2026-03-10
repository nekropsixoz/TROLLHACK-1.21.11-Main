package lol.ethane.mixin.client.entity;

import lol.ethane.Ethane;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.defined.movement.MovementFixModule;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1297.class})
public class EntityMixin {
   @Shadow
   private class_1937 field_6002;

   @Inject(
      method = {"method_36456"},
      at = {@At("HEAD")}
   )
   private void setYaw(float yaw, CallbackInfo ci) {
      if (class_310.method_1551().field_1724 != null && (Object)this == class_310.method_1551().field_1724 && this.field_6002.method_8608()) {
         RotationHelper.getClientHandler().onRotationSet();
      }

   }

   @Inject(
      method = {"method_36457"},
      at = {@At("HEAD")}
   )
   private void setPitch(float pitch, CallbackInfo ci) {
      if (class_310.method_1551().field_1724 != null && (Object)this == class_310.method_1551().field_1724 && this.field_6002.method_8608()) {
         RotationHelper.getClientHandler().onRotationSet();
      }

   }

   @Redirect(
      method = {"method_5724"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_36454()F"
)
   )
   private float redirectYaw(class_1297 instance) {
      boolean isPlayer = class_310.method_1551().field_1724 != null && (Object)this == class_310.method_1551().field_1724;
      return isPlayer && !((MovementFixModule)Ethane.getInstance().getModuleRepository().getModule(MovementFixModule.class)).isEnabled() ? RotationHelper.getClientHandler().getYawOr(instance.method_36454()) : instance.method_36454();
   }
}
