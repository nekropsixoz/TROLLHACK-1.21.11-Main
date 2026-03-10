package lol.ethane.mixin.input;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.press.KeyPressEvent;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_309.class})
public final class KeyboardHandlerMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"method_1466"}
   )
   public void onKey(long window, int action, class_11908 keyEvent, CallbackInfo ci) {
      if (action == 1) {
         if (keyEvent.comp_4795() == -1) {
            return;
         }

         EventDispatcher.dispatch(new KeyPressEvent(keyEvent.comp_4795()));
      }

   }
}
