package lol.ethane.mixin.input;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.press.MoveInputEvent;
import net.minecraft.class_10185;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_743.class})
public final class KeyboardInputMixin {
   @Unique
   private MoveInputEvent moveInputEvent;

   @Redirect(
      method = {"method_3129"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_304;method_1434()Z",
   ordinal = 4
)
   )
   private boolean hookMoveInputEventJump(class_304 instance) {
      return this.moveInputEvent != null && this.moveInputEvent.isJump();
   }

   @Redirect(
      method = {"method_3129"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_743;method_40218(ZZ)F",
   ordinal = 0
)
   )
   private float hookMoveInputEventForward(boolean positive, boolean negative) {
      return this.moveInputEvent == null ? 0.0F : this.moveInputEvent.getForward();
   }

   @Redirect(
      method = {"method_3129"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_743;method_40218(ZZ)F",
   ordinal = 1
)
   )
   private float hookMoveInputEventStrafe(boolean positive, boolean negative) {
      return this.moveInputEvent == null ? 0.0F : this.moveInputEvent.getSideways();
   }

   @ModifyExpressionValue(
      method = {"method_3129"},
      at = {@At(
   value = "NEW",
   target = "(ZZZZZZZ)Lnet/minecraft/class_10185;"
)}
   )
   private class_10185 modifyInput(class_10185 original) {
      this.moveInputEvent = new MoveInputEvent(getMovementMultiplier(class_310.method_1551().field_1690.field_1894.method_1434(), class_310.method_1551().field_1690.field_1881.method_1434()), getMovementMultiplier(class_310.method_1551().field_1690.field_1913.method_1434(), class_310.method_1551().field_1690.field_1849.method_1434()), class_310.method_1551().field_1690.field_1903.method_1434(), original.comp_3164(), original.comp_3165());
      EventDispatcher.dispatch(this.moveInputEvent);
      return new class_10185(this.moveInputEvent.getForward() > 0.0F, this.moveInputEvent.getForward() < 0.0F, this.moveInputEvent.getSideways() > 0.0F, this.moveInputEvent.getSideways() < 0.0F, this.moveInputEvent.isJump(), this.moveInputEvent.isSneak(), this.moveInputEvent.isSprint());
   }

   @Unique
   private static float getMovementMultiplier(boolean positive, boolean negative) {
      if (positive == negative) {
         return 0.0F;
      } else {
         return positive ? 1.0F : -1.0F;
      }
   }
}
