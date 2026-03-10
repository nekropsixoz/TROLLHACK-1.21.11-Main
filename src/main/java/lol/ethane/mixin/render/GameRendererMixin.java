package lol.ethane.mixin.render;

import lol.ethane.Ethane;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_757.class})
public class GameRendererMixin {
   @Inject(
      method = {"method_3169"},
      at = {@At("HEAD")}
   )
   private void onResize(int i, int j, CallbackInfo ci) {
      if (Ethane.getInstance() != null) {
         ;
      }
   }
}
