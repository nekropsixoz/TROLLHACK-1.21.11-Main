package lol.ethane.mixin.input;

import com.llamalad7.mixinextras.sugar.Local;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.mouse.MouseUpdateEvent;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import net.minecraft.class_312;
import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public class MouseHandlerMixin {
   @Shadow
   private double field_1789;
   @Shadow
   private double field_1787;
   @Unique
   private MouseUpdateEvent event;
   @Unique
   private boolean unlockCursor;

   private MouseHandlerMixin() {
   }

   @Redirect(
      method = {"method_1606"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_315;field_1914:Z",
   opcode = 180
)
   )
   private boolean turnPlayer(class_315 instance, @Local(ordinal = 3) double multiplier) {
      this.event = new MouseUpdateEvent(this.field_1789, this.field_1787, multiplier, this.unlockCursor, false);
      EventDispatcher.dispatch(this.event);
      return instance.field_1914;
   }

   @Redirect(
      method = {"method_55793"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_312;method_1613()Z"
)
   )
   private boolean redirectTickCursorLock(class_312 instance) {
      if (instance.method_1613()) {
         return true;
      } else if (RotationHelper.getHandler().isUnlockCursor()) {
         this.unlockCursor = true;
         return true;
      } else {
         return false;
      }
   }

   @Inject(
      method = {"method_1606"},
      at = {@At("TAIL")}
   )
   private void turnPlayer(double timeDelta, CallbackInfo ci) {
      RotationHelper.getClientHandler().onPostMouseUpdate();
      this.unlockCursor = false;
      this.event = null;
   }

   @Redirect(
      method = {"method_1606"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_312;field_1789:D",
   opcode = 180
)
   )
   private double redirectCursorX(class_312 instance) {
      return this.event == null ? 0.0D : this.event.getDeltaX();
   }

   @Redirect(
      method = {"method_1606"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_312;field_1787:D",
   opcode = 180
)
   )
   private double redirectCursorY(class_312 instance) {
      return this.event == null ? 0.0D : this.event.getDeltaY();
   }
}
