package lol.ethane.mixin.render;

import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_761.class})
public class LevelRendererMixin {
   @Redirect(
      method = {"method_62907"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_310;method_29611()Z"
)
   )
   private boolean redirectFabulousGraphics() {
      return true;
   }
}
