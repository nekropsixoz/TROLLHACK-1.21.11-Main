package lol.ethane.mixin.render;

import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_367;
import net.minecraft.class_8779;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_367.class})
public class AdvancementToastMixin {
   @Shadow
   @Final
   private class_8779 field_2205;

   @Inject(
      method = {"method_1986"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void render(class_332 guiGraphics, class_327 font, long l, CallbackInfo ci) {
      if (this.field_2205.comp_1919().method_12832().equals("story/mine_wood")) {
         ci.cancel();
      }

   }
}
